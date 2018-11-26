package slawomir.qstra.ble.bluetooth.bluetooth

import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import slawomir.qstra.ble.model.BluetoothState
import slawomir.qstra.ble.utils.Constants.Companion.LAMP
import timber.log.Timber

class BluetoothDeviceManagerImpl(context: Context) : BluetoothDeviceManager {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val scanner = bluetoothAdapter.bluetoothLeScanner

    val scanningState = MutableLiveData<BluetoothState>()

    private val scannerCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.e("onScanFailed: %s", errorCode)
            scanningState.value = BluetoothState.ScanningError
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                Timber.e("onScanResult %s", Gson().toJson(result.device))
                if (result.device.name == LAMP)
                    stopScanning()
            }
        }
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

    override fun getObservableBluetoothState() = scanningState

    override fun bluetoothEnable(): Boolean = bluetoothAdapter.isEnabled
}
