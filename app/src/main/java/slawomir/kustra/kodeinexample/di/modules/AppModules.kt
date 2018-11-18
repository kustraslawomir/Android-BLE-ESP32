package slawomir.kustra.kodeinexample.di.modules

import android.content.Context
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import slawomir.kustra.kodeinexample.ui.activity.vm.ScannerViewModel
import slawomir.kustra.kodeinexample.ui.activity.vm.ScannerViewModelFactory
import slawomir.kustra.kodeinexample.utils.logger.Logger
import slawomir.kustra.kodeinexample.utils.logger.LoggerIml

/*
Binding
 */
fun appModule(context: Context) = Kodein.Module {

    bind<Logger>() with singleton { LoggerIml() }

    bind<ScannerViewModelFactory>() with singleton { ScannerViewModelFactory() }

    bind() from provider { ScannerViewModelFactory().create(ScannerViewModel::class.java) }
}