package slawomir.kustra.ble.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import slawomir.kustra.ble.utils.Constants.Companion.LAMP
import slawomir.kustra.ble.utils.Constants.Companion.LENGTH_OF_SCANNER_LIFE
import slawomir.kustra.ble.utils.logger.Logger

class BluetoothScanner(
    kodein: Kodein,
    private val activity: AppCompatActivity,
    private val minSignalStrength: Int
) {

    private val logger: Logger by kodein.instance()

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

            logger.log("onScanResult -> ", result)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            logger.log("onBatchScanResults -> ", results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            logger.log("onScanFailed errorCode -> ", errorCode)
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
        logger.log("startScanning")
        scanning.value = true
        bluetoothLeScanner?.startScan(scanCallback)
    }

    internal fun stopScanning() {
        logger.log("stopScanning")
        scanning.value = false
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun isScanning() = scanning.value ?: false
    fun shouldCreateNewPair(): Boolean {
        var shouldCreateNewPair = true

        val pairedDevices = bluetoothAdapter.bondedDevices
        if (pairedDevices.size > 0) {
            pairedDevices.forEach { device ->
                run {
                    if (device.name == LAMP)
                        shouldCreateNewPair = false
                }
            }
        }
        return shouldCreateNewPair
    }
}