package slawomir.kustra.ble.data.model

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceModel(
    val device: BluetoothDevice,
    val rssi: Int
)