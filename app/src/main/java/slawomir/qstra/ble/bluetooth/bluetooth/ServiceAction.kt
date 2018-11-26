package slawomir.qstra.ble.bluetooth.bluetooth

import android.bluetooth.BluetoothGatt

internal interface ServiceAction {

        fun execute(bluetoothGatt: BluetoothGatt): Boolean

        companion object {
            val action: ServiceAction = object : ServiceAction {
                override fun execute(bluetoothGatt: BluetoothGatt): Boolean {
                    return true
                }
            }
        }
    }