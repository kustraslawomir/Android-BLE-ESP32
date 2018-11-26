package slawomir.qstra.ble

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import slawomir.qstra.ble.di.modules.appModule
import slawomir.qstra.ble.utils.logger.Logger
import timber.log.Timber

class BluetoothScannerApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(appModule(applicationContext))
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}