package slawomir.kustra.kodeinexample.ui.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import slawomir.kustra.kodeinexample.R
import slawomir.kustra.kodeinexample.bluetooth.BluetoothScanner
import slawomir.kustra.kodeinexample.ui.activity.broadcasts.BluetoothStateChangeReceiver
import slawomir.kustra.kodeinexample.ui.activity.vm.ScannerViewModel
import slawomir.kustra.kodeinexample.ui.activity.vm.ScannerViewModelFactory
import slawomir.kustra.kodeinexample.utils.Constants
import slawomir.kustra.kodeinexample.utils.logger.Logger

class ScannerActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private val logger by instance<Logger>()

    private lateinit var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver

    private val scannerViewModelFactory: ScannerViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewModelProviders.of(this, scannerViewModelFactory).get(ScannerViewModel::class.java)

        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver(this, logger)
        val scanner = BluetoothScanner(this, -75, logger)

        scanner.scanning.observe(this, Observer<Boolean> { scanning -> displayScanningState(scanning) })

        scanner.scannedDevices.observe(
            this,
            Observer<HashMap<String, BluetoothDevice>> { map -> displayDevicesList(map) })


        startScanning.setOnClickListener { scanner.startScanning() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            logger.log("BLE turned on")
        } else if (requestCode == Constants.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            logger.log("BLE turned off")
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(bluetoothStateChangeReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(bluetoothStateChangeReceiver)
    }

    private fun displayScanningState(scanning: Boolean) {
        if (scanning)
            scannedDevices.text = getString(R.string.scanning)
        else scannedDevices.text = getString(R.string.start_scanning_by_pressing_the_button)
    }

    private fun displayDevicesList(map: HashMap<String, BluetoothDevice>?) {
        if (map != null) {
            val stringBuilder = StringBuilder()

            map.forEach { (key, value) ->
                stringBuilder.append("Device name: $key\nDevice address: ${value.address}\n\n")
            }
            scannedDevices.text = stringBuilder.toString()
        }
    }
}
