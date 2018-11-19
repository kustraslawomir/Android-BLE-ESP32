package slawomir.kustra.ble.di.modules

import android.content.Context
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import slawomir.kustra.ble.ui.activity.vm.ScannerViewModel
import slawomir.kustra.ble.ui.activity.vm.ScannerViewModelFactory
import slawomir.kustra.ble.utils.logger.Logger
import slawomir.kustra.ble.utils.logger.LoggerIml

/*
Binding
 */
fun appModule(context: Context) = Kodein.Module {

    bind<Logger>() with singleton { LoggerIml() }

    bind<ScannerViewModelFactory>() with singleton { ScannerViewModelFactory() }

    bind() from provider { ScannerViewModelFactory().create(ScannerViewModel::class.java) }
}