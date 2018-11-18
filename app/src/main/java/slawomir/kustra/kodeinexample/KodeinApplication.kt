package slawomir.kustra.kodeinexample

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import slawomir.kustra.kodeinexample.di.modules.appModule
import slawomir.kustra.kodeinexample.utils.logger.Logger

class KodeinApplication : Application(), KodeinAware {

    private val logger by instance<Logger>()

    /*
    We uses modules to segregate dependencies.
     */
    override val kodein = Kodein.lazy {
        import(appModule(applicationContext))
    }

    override fun onCreate() {
        super.onCreate()
        logger.log("KodeinApplication", Logger.Level.Info, "onCreate")
    }
}