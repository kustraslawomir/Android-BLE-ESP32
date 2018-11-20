package slawomir.kustra.ble.ui.lampscanner

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_splash.*
import slawomir.kustra.ble.R
import slawomir.kustra.ble.bluetooth.BluetoothScanner
import slawomir.kustra.ble.model.ScanningResults
import slawomir.kustra.ble.ui.activity.MainActivity
import slawomir.kustra.ble.utils.Constants.Companion.LAMP

class LampScannerFragment : Fragment() {

    lateinit var activity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scanner = BluetoothScanner(activity, -75)

        scanner.scanning.observe(this, Observer<Boolean> { scanning -> displayScanningState(ScanningResults.Scanning) })

        scanner.scannedDevices.observe(
            this,
            Observer<HashMap<String, BluetoothDevice>> { map -> mapDevicesList(map, scanner) })

        scanner.initializeScanning()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    private fun mapDevicesList(
        map: HashMap<String, BluetoothDevice>,
        scanner: BluetoothScanner
    ) {
        map.forEach { (key, value) ->
            if (key == LAMP) {
                scanner.stopScanning()
                storeLamp(value)
                displayScanningState(ScanningResults.ScanningSuccess)
            }
        }
    }

    private fun storeLamp(value: BluetoothDevice) {
        activity.logger.log("found a lamp ${value.address}")
    }

    private fun displayScanningState(scanning: ScanningResults) {
        when (scanning) {
            ScanningResults.Scanning -> scanningTextView.text = getString(R.string.scanning)
            ScanningResults.ScanningSuccess -> scanningTextView.text = getString(R.string.scanning_success)
            ScanningResults.ScanningError -> scanningTextView.text = getString(R.string.scanning_error)

        }
    }
}