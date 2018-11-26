package slawomir.qstra.ble.bluetooth.bluetooth

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import slawomir.qstra.ble.model.BluetoothState

interface BluetoothDeviceManager {
    fun startScanning()
    fun stopScanning()
    fun getObservableBluetoothState(): MutableLiveData<BluetoothState>
    fun getObservableBluetoothDevice(): MutableLiveData<BluetoothDevice>
    fun bluetoothEnable(): Boolean
    fun disconnectBluetoothGatt()
    fun closeBluetoothGatt()
    fun send(checked: Boolean)
}
