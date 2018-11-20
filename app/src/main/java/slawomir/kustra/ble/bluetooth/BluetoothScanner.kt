package slawomir.kustra.ble.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import slawomir.kustra.ble.utils.Constants.Companion.LENGTH_OF_SCANNER_LIFE

class BluetoothScanner(
    private val activity: AppCompatActivity,
    private val minSignalStrength: Int) {

    /*
    Bluetooth adapter represents BT 'radio'
     */
    private val bluetoothAdapter: BluetoothAdapter

    private var bluetoothLeScanner: BluetoothLeScanner? = null

    internal var scannedDevices: MutableLiveData<HashMap<String, BluetoothDevice>> = MutableLiveData()
    internal var scanning: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val manager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = manager.adapter
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            var devicesHashMap = scannedDevices.value
            if (devicesHashMap == null)
                devicesHashMap = hashMapOf()

            val signalStrength = result.rssi
            val device = result.device

            if (signalStrength > minSignalStrength) {
                devicesHashMap[device.name] = result.device
                scannedDevices.value = devicesHashMap
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }

    fun initializeScanning() {
        if (BluetoothUtils.checkBluetooth(bluetoothAdapter))
            scanDevices(true)
        else
            BluetoothUtils.requestBluetooth(activity)
    }


    private fun scanDevices(enable: Boolean) {
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        if (enable && !isScanning()) {
            GlobalScope.launch(Dispatchers.Main) {
                delay(LENGTH_OF_SCANNER_LIFE)
                stopScanning()
            }
            startScanning()
        }
    }

    private fun startScanning() {
        scanning.value = true
        bluetoothLeScanner?.startScan(scanCallback)
    }

    internal fun stopScanning() {
        scanning.value = false
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun isScanning() = scanning.value ?: false
}