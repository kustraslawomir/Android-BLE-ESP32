package slawomir.kustra.ble.ui.fragments.devicedetails

import android.bluetooth.*
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import slawomir.kustra.ble.R
import slawomir.kustra.ble.bluetooth.GattServerCallback
import slawomir.kustra.ble.ui.activity.MainActivity
import slawomir.kustra.ble.utils.Constants
import timber.log.Timber
import java.util.*


class DeviceDetailsFragment : Fragment() {

    lateinit var activity: MainActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_device_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bluetoothDevice = arguments?.getParcelable<BluetoothDevice>(Constants.DEVICE)
        if (bluetoothDevice != null) {
            connectDevice(bluetoothDevice)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    private fun connectDevice(bluetoothDevice: BluetoothDevice) {
        activity.logger.log("try to connect device")

        val manager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = manager.adapter

        val device = bluetoothAdapter.getRemoteDevice(bluetoothDevice.address)

        if (device == null) {
            Timber.e("device is null")
            return
        }

        device.connectGatt(activity, false, connectionCallback())
    }

    private fun connectionCallback() = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Timber.i("connected to gatt service | ${gatt?.discoverServices()}")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Timber.i("disconnected from gatt service")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.i("onServiceDiscovered")
            } else {
                Timber.i("onServiceDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
            Timber.i("onCharacteristicChanged  ${characteristic.uuid} value: ${characteristic.value}")
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Timber.i("onCharacteristicRead ${characteristic.uuid} value: ${characteristic.value}")
                }
                BluetoothGatt.GATT_FAILURE ->{
                    Timber.e("onCharacteristicRead -> GATT_FAILURE")
                }
            }
        }
    }
}