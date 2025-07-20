package com.example.bluetoothmessenger.domain.chat

sealed class ConnectionResult {
    object ConnectionEstablished : ConnectionResult()
    data class Error(val message: String) : ConnectionResult()

    data class TranferSuccedded(val message: BluetoothMessage) : ConnectionResult()
}