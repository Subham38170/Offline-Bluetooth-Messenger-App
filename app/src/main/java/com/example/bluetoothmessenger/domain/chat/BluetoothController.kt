package com.example.bluetoothmessenger.domain.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevice: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    val isConnected: StateFlow<Boolean>
    val error : SharedFlow<String>

    fun startDiscovery()
    fun stopDiscovery()

    fun release()

    fun startBluetoothServer(): Flow<ConnectionResult>

    fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult>

    fun closeConnection()


    suspend fun trySendMessage(message: String): BluetoothMessage?
}