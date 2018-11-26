package slawomir.qstra.ble.model

sealed class ScanningResults {

    object Scanning : ScanningResults()
    object ScanningSuccess : ScanningResults()
    object ScanningError : ScanningResults()
}