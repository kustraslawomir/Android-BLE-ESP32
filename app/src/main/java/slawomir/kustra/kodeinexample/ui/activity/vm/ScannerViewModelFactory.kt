package slawomir.kustra.kodeinexample.ui.activity.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScannerViewModelFactory() : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ScannerViewModel() as T
    }
}