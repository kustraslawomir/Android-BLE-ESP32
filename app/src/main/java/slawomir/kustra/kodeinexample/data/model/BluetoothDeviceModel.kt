package slawomir.kustra.kodeinexample.data.model

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceModel(
    val device: BluetoothDevice,
    val rssi: Int
)