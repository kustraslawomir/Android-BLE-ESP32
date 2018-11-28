package slawomir.qstra.ble.bluetooth.bluetooth

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import slawomir.qstra.ble.bluetooth.bluetooth.BluetoothUtils.bytesToHex
import slawomir.qstra.ble.model.BluetoothState
import slawomir.qstra.ble.utils.Constants.Companion.LAMP
import timber.log.Timber
import java.util.*

class BluetoothDeviceManagerImpl(private val context: Context) : BluetoothDeviceManager, BluetoothGattCallback() {

    private lateinit var gattConnection: BluetoothGatt

    val ESP_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
    val ESP_SERVICE_CHARACTERISTICS_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val scanner = bluetoothAdapter.bluetoothLeScanner

    private val scanningState = MutableLiveData<BluetoothState>()
    private val connectedDevice = MutableLiveData<BluetoothDevice>()

    private val commandQueue = LinkedList<ServiceAction>()

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
                if (result.device.name == LAMP && scanningState.value != BluetoothState.Connecting) {
                    stopScanning()
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(500)
                        connectToDevice(result.device)
                    }
                }
            }
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        Timber.e("connecting...")
        scanningState.value = BluetoothState.Connecting
        gattConnection = device.connectGatt(context, true, this, BluetoothDevice.TRANSPORT_LE)
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
        GlobalScope.launch(Dispatchers.Main) {
            handleConnectionState(gatt.device, newState)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Timber.e("onServicesDiscovered: %s", status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            val mBluetoothGattService = gattConnection.getService(UUID.fromString(ESP_UUID))
            if (mBluetoothGattService != null)
                Timber.e("onServicesDiscovered for uuid: %s", mBluetoothGattService.uuid.toString())
            else
                Timber.e("onServicesDiscovered -> unable to get service for uuid: %s", ESP_UUID)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        super.onCharacteristicChanged(gatt, characteristic)
        Timber.e("onCharacteristicChanged")
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
        Timber.e("onDescriptorWrite")
    }

    override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        super.onDescriptorRead(gatt, descriptor, status)
        Timber.e("onDescriptorRead")
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        Timber.e("onCharacteristicWrite: %s", BluetoothUtils.bytesToHex(characteristic?.value))
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        Timber.e("onCharacteristicRead")
    }

    private fun handleConnectionState(device: BluetoothDevice, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                Timber.e("STATE_CONNECTED")
                gattConnection.discoverServices()
                scanningState.value = BluetoothState.Connected
                connectedDevice.value = device
            }
            BluetoothProfile.STATE_DISCONNECTED -> {
                Timber.e("STATE_DISCONNECTED")
                scanningState.value = BluetoothState.ConnectingError
            }
        }
    }

    override fun send(checked: Boolean) {
        var value = "0"
        if (checked)
            value = "1"

        val action = serviceWriteAction(getGattService(ESP_UUID), ESP_SERVICE_CHARACTERISTICS_UUID, value.toByteArray())
        commandQueue.add(action)
        execute(gattConnection)
    }

    private fun serviceWriteAction(
        gattService: BluetoothGattService,
        uuid: String,
        value: ByteArray
    ): ServiceAction {
        return object : ServiceAction {
            override fun execute(bluetoothGatt: BluetoothGatt): Boolean {
                Timber.e("execute: %s", bytesToHex(value))
                val characteristicUuid = UUID.fromString(uuid)
                val characteristic = gattService.getCharacteristic(characteristicUuid)
                return if (characteristic != null) {
                    characteristic.value = value
                    Timber.e("writeCharacteristic %s", Gson().toJson(characteristic.value))
                    val write = bluetoothGatt.writeCharacteristic(characteristic)
                    Timber.e("write returns: %s", write)
                    false
                } else {
                    Timber.e("write: characteristic not found: %s", uuid)
                    true
                }
            }
        }
    }

    private fun execute(gatt: BluetoothGatt) {
        for (i in commandQueue.indices) {
            commandQueue[i].execute(gatt)
        }
    }

    private fun getGattService(uuid: String): BluetoothGattService {
        Timber.e("get gatt service: %s", uuid)
        val serviceUuid = UUID.fromString(uuid)
        return gattConnection.getService(serviceUuid)
    }


}