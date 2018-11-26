package slawomir.qstra.ble.model

sealed class BluetoothState {

    object Scanning : BluetoothState()
    object ScanningError : BluetoothState()

    object Connecting : BluetoothState()
    object Connected : BluetoothState()
    object ConnectingError : BluetoothState()
}