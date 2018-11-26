package slawomir.qstra.ble.ui.fragments.devicedetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import slawomir.qstra.ble.R
import slawomir.qstra.ble.ui.activity.MainActivity

class DeviceDetailsFragment : Fragment() {

    lateinit var activity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_device_details, container, false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}