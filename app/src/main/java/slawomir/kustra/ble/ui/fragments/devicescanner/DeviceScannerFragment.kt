package slawomir.kustra.ble.ui.fragments.devicescanner

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import slawomir.kustra.ble.R
import slawomir.kustra.ble.bluetooth.BluetoothScanner
import slawomir.kustra.ble.model.ScanningResults
import slawomir.kustra.ble.ui.activity.MainActivity
import slawomir.kustra.ble.ui.fragments.devicedetails.DeviceDetailsFragment
import slawomir.kustra.ble.utils.Constants.Companion.DEVICE
import slawomir.kustra.ble.utils.Constants.Companion.LAMP
import timber.log.Timber

class DeviceScannerFragment : Fragment() {

    lateinit var activity: MainActivity

    private lateinit var scanner: BluetoothScanner

    private var lampDevice: BluetoothDevice? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scanner = BluetoothScanner(activity.kodein, activity, -75)

        scanner.scanning.observe(this, Observer<Boolean> { scanning ->
            if (scanning)
                displayScanningState(ScanningResults.Scanning)
        })

        scanner.scannedDevices.observe(
            this,
            Observer<HashMap<String, BluetoothDevice>> { map -> mapDevicesList(map, scanner) })

        scanner.initializeScanning()

        activity.registerReceiver(pairReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onDestroy() {
        scanner.stopScanning()
        activity.unregisterReceiver(pairReceiver)
        super.onDestroy()
    }

    private fun mapDevicesList(
        map: HashMap<String, BluetoothDevice>,
        scanner: BluetoothScanner
    ) {
        map.forEach { (key, value) ->
            Timber.e("key: %s", key == LAMP)
            when (key) {
                LAMP -> {
                    this.lampDevice = value
                    scanner.stopScanning()
                    pairDevice(value)
                }
            }
        }
    }

    private fun pairDevice(device: BluetoothDevice) {
        Timber.e("pair device")
        if (scanner.shouldCreateNewPair())
            createBond(device)
        else openDeviceDetailsScreen(device)
    }

    private fun openDeviceDetailsScreen(device: BluetoothDevice) {
        Timber.e("openDeviceDetailsScreen")
        val bundle = Bundle()
        bundle.putParcelable(DEVICE, device)
        val deviceDetailsFragment = DeviceDetailsFragment()
        deviceDetailsFragment.arguments = bundle

        GlobalScope.launch(Dispatchers.IO) {
            delay(1500)
            activity.replaceFragment(deviceDetailsFragment)
        }
    }

    private fun displayScanningState(scanning: ScanningResults) {
        when (scanning) {
            ScanningResults.Scanning -> scanningTextView.text = getString(R.string.scanning)
            ScanningResults.ScanningSuccess -> scanningTextView.text = getString(R.string.scanning_success)
            ScanningResults.ScanningError -> scanningTextView.text = getString(R.string.scanning_error)
        }
    }

    private val pairReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {

                val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                val prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)

                when (state) {
                    BOND_BONDED -> { lampDevice?.let { openDeviceDetailsScreen(it) } }
                    BOND_NONE -> {
                        if (prevState == BluetoothDevice.BOND_BONDED) {
                            Toast.makeText(context, "Unpaired", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun createBond(btDevice: BluetoothDevice): Boolean {
        val class1 = Class.forName("android.bluetooth.BluetoothDevice")
        val createBondMethod = class1.getMethod("createBond")
        return createBondMethod.invoke(btDevice) as Boolean
    }
}