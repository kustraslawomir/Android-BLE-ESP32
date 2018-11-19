package slawomir.kustra.kodeinexample.ui.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import slawomir.kustra.kodeinexample.R
import slawomir.kustra.kodeinexample.bluetooth.BluetoothScanner
import slawomir.kustra.kodeinexample.bluetooth.ScannerCallback
import slawomir.kustra.kodeinexample.ui.activity.broadcasts.BluetoothStateChangeReceiver
import slawomir.kustra.kodeinexample.ui.activity.vm.ScannerViewModel
import slawomir.kustra.kodeinexample.ui.activity.vm.ScannerViewModelFactory
import slawomir.kustra.kodeinexample.utils.Constants
import slawomir.kustra.kodeinexample.utils.logger.Logger

class ScannerActivity : AppCompatActivity(), KodeinAware, ScannerCallback {
    override val kodein by closestKodein()

    private val logger by instance<Logger>()

    private lateinit var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver

    private val scannerViewModelFactory: ScannerViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this, scannerViewModelFactory).get(ScannerViewModel::class.java)

        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver(this, logger)

        val scanner = BluetoothScanner(this, 5000, -75, this, logger)
        scanner.startScanning()

        fab.setOnClickListener {
            logger.log("scanner", Logger.Level.Verbose, "fab clicked")
        }
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

    override fun stopScanning() {

    }

    override fun addDevice(device: BluetoothDevice) {
        logger.log("added new device!  ${device.name}")
    }
}
