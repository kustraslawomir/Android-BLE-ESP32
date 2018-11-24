package slawomir.kustra.ble

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import slawomir.kustra.ble.di.modules.appModule
import slawomir.kustra.ble.utils.logger.Logger
import timber.log.Timber

class BluetoothScannerApplication : Application(), KodeinAware {

    private val logger by instance<Logger>()

    override val kodein = Kodein.lazy {
        import(appModule(applicationContext))
    }

    override fun onCreate() {
        super.onCreate()
        logger.log("BluetoothScannerApplication", Logger.Level.Info, "onCreate")
        Timber.plant(Timber.DebugTree())
    }
}