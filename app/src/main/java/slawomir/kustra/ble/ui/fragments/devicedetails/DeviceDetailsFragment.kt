package slawomir.kustra.ble.ui.fragments.devicedetails

import android.bluetooth.*
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_device_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import slawomir.kustra.ble.R
import slawomir.kustra.ble.ui.activity.MainActivity
import slawomir.kustra.ble.utils.Constants
import slawomir.kustra.ble.utils.Constants.Companion.ESP_UUID
import timber.log.Timber
import java.io.IOException
import java.io.OutputStream
import java.util.*

class DeviceDetailsFragment : Fragment() {

    lateinit var activity: MainActivity

    private lateinit var espOutStream: OutputStream

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_device_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bluetoothDevice = arguments?.getParcelable<BluetoothDevice>(Constants.DEVICE)
        if (bluetoothDevice != null) {
            GlobalScope.launch(Dispatchers.IO) {
                delay(500)
                connectDevice(bluetoothDevice)
            }
        }
        ledLight.setOnCheckedChangeListener { _, checked ->
            changeLedState(checked)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    private fun connectDevice(bluetoothDevice: BluetoothDevice) {
        val manager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = manager.adapter

        val device = bluetoothAdapter.getRemoteDevice(bluetoothDevice.address)

        if (device == null) {
            Timber.e("device is null")
            return
        }

        bluetoothAdapter.cancelDiscovery()
        createSocketListener(device)
        device.connectGatt(activity, false, connectionCallback())
    }

    private fun connectionCallback() = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Timber.e("onConnectionStateChange: %s", newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Timber.i("connected to gatt service | ${gatt.discoverServices()}")
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Timber.i("disconnected from gatt service")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.i("onServiceDiscovered")
            } else {
                Timber.i("onServiceDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Timber.i("onCharacteristicChanged  ${characteristic.uuid} value: ${characteristic.value}")
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Timber.i("onCharacteristicRead ${characteristic.uuid} value: ${characteristic.value}")
                }
                BluetoothGatt.GATT_FAILURE -> {
                    Timber.e("onCharacteristicRead -> GATT_FAILURE")
                }
            }
        }
    }

    private fun createSocketListener(connectedDevice: BluetoothDevice) {
        Timber.e("createSocketListener")

        val tmp = createBluetoothSocket(connectedDevice)
        val clazz = tmp.remoteDevice::class.java
        val paramTypes = arrayOf<Class<*>>(Integer.TYPE)

        val m = clazz.getMethod("createRfcommSocket", *paramTypes)
        val params = arrayOf<Any>(Integer.valueOf(1))

        val fallbackSocket = m.invoke(tmp.remoteDevice, *params) as BluetoothSocket

        try {
            Timber.e("connect socket")
            fallbackSocket.connect()
        } catch (e: IOException) {
            fallbackSocket.close()
            Timber.e("socket connection error: %s", e.message)
        }

        try {
            espOutStream = fallbackSocket.outputStream
        } catch (e: IOException) {
            Timber.e("out stream error: %s", e.message)
        }
    }

    private fun createBluetoothSocket(connectedDevice: BluetoothDevice): BluetoothSocket =
        connectedDevice.createRfcommSocketToServiceRecord(UUID.fromString(ESP_UUID))

    private fun changeLedState(lighted: Boolean) {
        if (::espOutStream.isInitialized) {
            val message = getLightValue(lighted)
            val bytes = message.toByteArray()

            Timber.e("send message: %s", message)

            try {
                espOutStream.write(bytes)
            } catch (e: IOException) {
                Timber.e("sending light state error: %s ", e.message)
            }
        }
    }

    private fun getLightValue(lighted: Boolean): String {
        var value = "0"
        if (lighted)
            value = "1"
        return value
    }
}