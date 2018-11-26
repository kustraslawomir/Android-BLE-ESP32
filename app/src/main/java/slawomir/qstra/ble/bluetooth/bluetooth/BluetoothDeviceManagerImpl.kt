package slawomir.qstra.ble.bluetooth.bluetooth

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import slawomir.qstra.ble.model.BluetoothState
import slawomir.qstra.ble.utils.Constants.Companion.LAMP
import timber.log.Timber

class BluetoothDeviceManagerImpl(private val context: Context) : BluetoothDeviceManager, BluetoothGattCallback() {

    private lateinit var gattConnection: BluetoothGatt

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val scanner = bluetoothAdapter.bluetoothLeScanner

    private val scanningState = MutableLiveData<BluetoothState>()
    private val connectedDevice = MutableLiveData<BluetoothDevice>()

    private val scannerCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.e("onScanFailed: %s", errorCode)
            scanningState.value = BluetoothState.ScanningError
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                Timber.e("onScanResult %s", Gson().toJson(result.device.name))
                if (result.device.name == LAMP) {
                    stopScanning()
                    connectToDevice(result.device)
                }
            }
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        scanningState.value = BluetoothState.Connecting
        gattConnection = device.connectGatt(context, false, this)
    }

    override fun startScanning() {
        Timber.e("startScanning")
        scanningState.value = BluetoothState.Scanning
        scanner.startScan(scannerCallback)
    }

    override fun stopScanning() {
        Timber.e("stop scanning")
        scanner.stopScan(scannerCallback)
    }

    override fun disconnectBluetoothGatt() {
        if (::gattConnection.isInitialized)
            gattConnection.disconnect()
    }

    override fun closeBluetoothGatt() {
        if (::gattConnection.isInitialized)
            gattConnection.disconnect()
    }

    override fun getObservableBluetoothState() = scanningState

    override fun getObservableBluetoothDevice(): MutableLiveData<BluetoothDevice> = connectedDevice

    override fun bluetoothEnable(): Boolean = bluetoothAdapter.isEnabled

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        Timber.e("onConnectionStateChange: status: %ss | newState: %s", status, newState)
        GlobalScope.launch(Dispatchers.Main){
            handleConnectionState(gatt.device, newState)
        }
    }

    private fun handleConnectionState(device: BluetoothDevice, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                scanningState.value = BluetoothState.Connected
                connectedDevice.value = device
            }
            BluetoothProfile.STATE_DISCONNECTED -> {
                scanningState.value = BluetoothState.ConnectingError
            }
        }
    }
}
