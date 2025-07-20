package com.example.bluetoothmessenger.data.chat

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import com.example.bluetoothmessenger.domain.chat.BluetoothController
import com.example.bluetoothmessenger.domain.chat.BluetoothDevice
import com.example.bluetoothmessenger.domain.chat.BluetoothDeviceDomain
import com.example.bluetoothmessenger.domain.chat.BluetoothMessage
import com.example.bluetoothmessenger.domain.chat.ConnectionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class AndroidBluetoothController(
    private val context: Context,

    ) : BluetoothController {
    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private val foundDeviceReceiver = FoundDeviceReciever { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices
            else devices + newDevice
        }
    }


    private val _isConnected = MutableStateFlow<Boolean>(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null


    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevice: StateFlow<List<BluetoothDevice>> = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val error: SharedFlow<String> = _errors.asSharedFlow()

    private var dataTransferService: BluetoothDataTransferService? = null
    private val bluetoothStateReciever = BluetoothStateReciver(
        onStateChange = @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT) { isConnected, device ->
            if (bluetoothAdapter?.bondedDevices?.contains(device) == true) {
                _isConnected.update { isConnected }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    _errors.emit("Can't connected to non-paired device")
                }
            }

        })

    init {

        updatePairedDevices()
        context.registerReceiver(
            bluetoothStateReciever, IntentFilter().apply {
                addAction(
                    BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED
                )
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }


        )
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        context.registerReceiver(
            foundDeviceReceiver, IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        )
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReciever)
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No Bluetooth_Connect Permission")
            }
            if (bluetoothAdapter?.isEnabled != true) {
                emit(ConnectionResult.Error("Bluetooth is OFF"))
                return@flow
            }

            currentServerSocket = bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(
                SERVICE_NAME, UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true
            while (shouldLoop) {
                try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null

                }
                emit(ConnectionResult.ConnectionEstablished)
                currentClientSocket?.let {
                    currentServerSocket?.close()
                    val service = BluetoothDataTransferService(it)
                    dataTransferService = service
                    emitAll(
                        service.listenForIncomingMessages()
                            .map { ConnectionResult.TranferSuccedded(it) })
                }
            }


        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTh_CONNECT Permission")
            }
            if (bluetoothAdapter?.isEnabled != true) {
                emit(ConnectionResult.Error("Bluetooth is OFF"))
                return@flow
            }

            val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)

            currentClientSocket = bluetoothAdapter?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))
            stopDiscovery()
            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)
                    BluetoothDataTransferService(socket)
                        .also {
                            dataTransferService = it

                            emitAll(
                                it.listenForIncomingMessages().map {
                                    ConnectionResult.TranferSuccedded(it)
                                }
                            )
                        }

                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }

        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun trySendMessage(message: String): BluetoothMessage? {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return null
        }
        if (dataTransferService == null) {
            return null
        }
        val bluetoothMessage = BluetoothMessage(
            message = message,
            senderName = bluetoothAdapter?.name ?: "Unknown Name",
            isFromLocalUser = true
        )
        dataTransferService?.sendMessage(bluetoothMessage.toByteArray())
        return bluetoothMessage
    }

    override fun closeConnection() {
        currentServerSocket?.close()
        currentClientSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        bluetoothAdapter?.bondedDevices?.map { it.toBluetoothDeviceDomain() }?.also { devices ->
            _pairedDevices.update { devices }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val SERVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB"
        const val SERVICE_NAME = "chat_server"
    }
}