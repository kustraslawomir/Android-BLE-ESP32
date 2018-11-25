package slawomir.kustra.ble.ui.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import slawomir.kustra.ble.R
import slawomir.kustra.ble.ui.activity.broadcasts.BluetoothStateChangeReceiver
import slawomir.kustra.ble.ui.activity.vm.ScannerViewModel
import slawomir.kustra.ble.ui.activity.vm.ScannerViewModelFactory
import slawomir.kustra.ble.ui.fragments.devicescanner.DeviceScannerFragment
import slawomir.kustra.ble.utils.Constants
import slawomir.kustra.ble.utils.logger.Logger
import timber.log.Timber


class MainActivity : AppCompatActivity(), KodeinAware {

    internal val logger by instance<Logger>()
    private val scannerViewModelFactory: ScannerViewModelFactory by instance()
    private lateinit var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver

    override val kodein by closestKodein()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewModelProviders.of(this, scannerViewModelFactory).get(ScannerViewModel::class.java)

        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver(this, logger)

        replaceFragment(DeviceScannerFragment())
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

    internal fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}