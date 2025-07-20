package com.example.bluetoothmessenger.data.chat

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BluetoothStateReciver
    (
    private val onStateChange: (isConnected: Boolean, BluetoothDevice) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java

            )
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(
                BluetoothDevice.EXTRA_NAME
            )
        }
        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {

                onStateChange(true, device ?: return)
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                onStateChange(false, device ?: return)
            }
        }
    }


}