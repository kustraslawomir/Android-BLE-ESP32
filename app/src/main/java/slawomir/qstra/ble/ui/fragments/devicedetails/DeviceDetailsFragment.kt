package slawomir.qstra.ble.ui.fragments.devicedetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_device_details.*
import slawomir.qstra.ble.R
import slawomir.qstra.ble.ui.activity.MainActivity

class DeviceDetailsFragment : Fragment() {

    lateinit var activity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_device_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ledLight.setOnCheckedChangeListener { _, checked ->
            activity.bluetoothDeviceManager.send(checked)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}