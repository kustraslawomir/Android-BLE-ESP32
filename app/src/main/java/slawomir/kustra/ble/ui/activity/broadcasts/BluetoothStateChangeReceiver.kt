package slawomir.kustra.ble.ui.activity.broadcasts

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import slawomir.kustra.ble.utils.logger.Logger

class BluetoothStateChangeReceiver(private val context: Context, private val logger: Logger) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

            when (state) {
                BluetoothAdapter.STATE_TURNING_ON -> logger.log("Bluetooth is on")
                BluetoothAdapter.STATE_ON -> logger.log("Bluetooth is on")
                BluetoothAdapter.STATE_TURNING_OFF -> logger.log("Bluetooth is turning off")
                BluetoothAdapter.STATE_OFF -> logger.log("Bluetooth is off")
                else -> logger.log(state.toString())
            }
        }
    }
}