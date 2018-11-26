package slawomir.qstra.ble.ui.fragments.devicescanner

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import slawomir.qstra.ble.R
import slawomir.qstra.ble.model.BluetoothState
import slawomir.qstra.ble.ui.activity.MainActivity
import slawomir.qstra.ble.ui.fragments.devicedetails.DeviceDetailsFragment
import slawomir.qstra.ble.utils.Constants.Companion.DEVICE
import timber.log.Timber

class DeviceScannerFragment : Fragment() {

    private lateinit var activity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.bluetoothDeviceManager.getObservableBluetoothState()
            .observe(this, Observer<BluetoothState> { bluetoothState ->
                when (bluetoothState) {
                    BluetoothState.Scanning -> showScanningUi()
                    BluetoothState.ScanningError -> showScanningError()
                    BluetoothState.Connected -> showConnectedUi()
                    BluetoothState.Connecting -> showConnectingUi()
                    BluetoothState.ConnectingError -> showScanningError()
                }
            })

        activity.bluetoothDeviceManager.getObservableBluetoothDevice()
            .observe(this, Observer<BluetoothDevice> { connectedDevice ->
                openDeviceDetailsScreen(connectedDevice)
            })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        activity.bluetoothDeviceManager.startScanning()
    }

    override fun onPause() {
        activity.bluetoothDeviceManager.stopScanning()
        super.onPause()
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

    private fun showConnectingUi() {
        scanningTextView.text = getString(R.string.connecting_error)
    }

    private fun showConnectedUi() {
        scanningTextView.text = getString(R.string.connected)
    }

    private fun showScanningError() {
        scanningTextView.text = getString(R.string.scanning_error)
    }

    private fun showScanningUi() {
        scanningTextView.text = getString(R.string.scanning)
    }
}