package slawomir.kustra.kodeinexample.bluetooth

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
import slawomir.kustra.kodeinexample.utils.Constants.Companion.LENGTH_OF_SCANNER_LIFE
import slawomir.kustra.kodeinexample.utils.logger.Logger


class BluetoothScanner(
    private val activity: AppCompatActivity,
    private val minSignalStrength: Int,
    private val stopScannerCallback: StopScannerCallback,
    private val logger: Logger
) {

    private val bluetoothAdapter: BluetoothAdapter

    private var scanning = false

    internal var scannedDevices: MutableLiveData<HashMap<String, BluetoothDevice>> = MutableLiveData()

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
                if (devicesHashMap.containsKey(device.name)) {
                    devicesHashMap.remove(device.name)
                }
                devicesHashMap[device.name] = result.device
                scannedDevices.value = devicesHashMap
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            logger.log("onBatchScanResults size: ${results.size}")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            logger.log("onScanFailed: $errorCode")
        }
    }

    fun startScanning() {
        if (BluetoothUtils.checkBluetooth(bluetoothAdapter))
            scanDevices(true)
        else {
            BluetoothUtils.requestBluetooth(activity)
            stopScannerCallback.stopScanning()
        }
    }

    private fun scanDevices(enable: Boolean) {
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        if (enable && !isScanning()) {
            GlobalScope.launch(Dispatchers.IO) {
                delay(LENGTH_OF_SCANNER_LIFE)
                stopScanning(bluetoothLeScanner)
            }
            startScanning(bluetoothLeScanner)
        }
    }

    private fun startScanning(bluetoothLeScanner: BluetoothLeScanner) {
        scanning = true
        bluetoothLeScanner.startScan(scanCallback)
    }

    private fun stopScanning(bluetoothLeScanner: BluetoothLeScanner) {
        scanning = false
        bluetoothLeScanner.stopScan(scanCallback)
    }

    private fun isScanning() = scanning
}