package com.example.bluetoothmessenger.data.chat

import android.Manifest
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import com.example.bluetoothmessenger.domain.chat.BluetoothDeviceDomain

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain{
    return BluetoothDeviceDomain(
        name = name,
        address = address

    )
}