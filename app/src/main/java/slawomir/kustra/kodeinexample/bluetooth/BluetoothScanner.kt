package slawomir.kustra.kodeinexample.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import slawomir.kustra.kodeinexample.utils.logger.Logger


class BluetoothScanner(
    private val activity: AppCompatActivity,
    private val scanPeriod: Long,
    private val signalStrength: Int,
    private val scannerCallback: ScannerCallback,
    private val logger: Logger
) {
    private val bluetoothAdapter: BluetoothAdapter
    private var scanning = false

    private val mLeScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            logger.log("found device with rssi: ${result.rssi}")

            if (result.rssi > signalStrength) {
                scannerCallback.addDevice(result.device)
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

    init {
        val manager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = manager.adapter
    }

    private fun isScanning() = scanning

    fun startScanning() {
        if (BluetoothUtils.checkBluetooth(bluetoothAdapter))
            scanDevices(true)
        else {
            BluetoothUtils.requestBluetooth(activity)
            scannerCallback.stopScanning()
        }
    }

    private fun scanDevices(enable: Boolean) {
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        if (enable && !isScanning()) {
            GlobalScope.launch(Dispatchers.IO) {
                delay(scanPeriod)
                stopScanning(bluetoothLeScanner)
            }
            scanning = true
            bluetoothLeScanner.startScan(mLeScanCallback)
        }
    }

    private fun stopScanning(bluetoothLeScanner: BluetoothLeScanner) {
        scanning = false
        bluetoothLeScanner.stopScan(mLeScanCallback)
    }
}