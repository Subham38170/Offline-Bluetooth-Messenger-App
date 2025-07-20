package com.example.bluetoothmessenger.presentation

import com.example.bluetoothmessenger.domain.chat.BluetoothDevice
import com.example.bluetoothmessenger.domain.chat.BluetoothMessage

data class BluetoothUIState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList()

)