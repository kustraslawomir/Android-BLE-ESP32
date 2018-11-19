package slawomir.kustra.kodeinexample.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import slawomir.kustra.kodeinexample.utils.Constants

class BluetoothUtils {

    companion object {

        fun checkBluetooth(bluetoothAdapter: BluetoothAdapter?): Boolean {
            return !(bluetoothAdapter == null || !bluetoothAdapter.isEnabled)
        }

        fun requestBluetooth(activity: AppCompatActivity) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT)
        }
    }
}