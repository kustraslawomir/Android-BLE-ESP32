package slawomir.qstra.ble.ui.activity

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import slawomir.qstra.ble.R
import slawomir.qstra.ble.bluetooth.bluetooth.BluetoothDeviceManager
import slawomir.qstra.ble.ui.activity.vm.ScannerViewModel
import slawomir.qstra.ble.ui.activity.vm.ScannerViewModelFactory
import slawomir.qstra.ble.ui.fragments.devicescanner.DeviceScannerFragment
import slawomir.qstra.ble.utils.Constants
import slawomir.qstra.ble.utils.Constants.Companion.REQUEST_ENABLE_BT
import slawomir.qstra.ble.utils.Constants.Companion.REQUEST_LOCATION_ACCESS
import slawomir.qstra.ble.utils.logger.Logger


class MainActivity : AppCompatActivity(), KodeinAware {

    private val logger by instance<Logger>()

    private val scannerViewModelFactory by instance<ScannerViewModelFactory>()
    internal val bluetoothDeviceManager by instance<BluetoothDeviceManager>()

    override val kodein by closestKodein()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewModelProviders.of(this, scannerViewModelFactory).get(ScannerViewModel::class.java)

        if (!bluetoothDeviceManager.bluetoothEnable()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return
        }

        if (!checkBlePermissions()) {
            requestLocationPermissions()
            return
        }

        replaceFragment(DeviceScannerFragment())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            logger.log("BLE turned on")
        } else if (requestCode == Constants.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            logger.log("BLE turned off")
        }
    }

    private fun checkBlePermissions(): Boolean {
        val accessFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val accessCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return accessFineLocation == PackageManager.PERMISSION_GRANTED && accessCoarseLocation == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION_ACCESS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_ACCESS -> if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    internal fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}