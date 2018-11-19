package slawomir.kustra.kodeinexample.bluetooth

import android.bluetooth.BluetoothDevice

interface ScannerCallback {

    fun stopScanning()

    fun addDevice(device: BluetoothDevice)

}