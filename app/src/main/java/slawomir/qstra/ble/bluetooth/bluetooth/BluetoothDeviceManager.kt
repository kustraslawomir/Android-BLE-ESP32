package slawomir.qstra.ble.bluetooth.bluetooth

import androidx.lifecycle.MutableLiveData
import slawomir.qstra.ble.model.BluetoothState

interface BluetoothDeviceManager {
    fun startScanning()
    fun stopScanning()
    fun getObservableBluetoothState(): MutableLiveData<BluetoothState>
    fun bluetoothEnable(): Boolean
}