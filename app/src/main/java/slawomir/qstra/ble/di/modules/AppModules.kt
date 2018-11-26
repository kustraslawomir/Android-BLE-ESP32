package slawomir.qstra.ble.di.modules

import android.content.Context
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import slawomir.qstra.ble.bluetooth.bluetooth.BluetoothDeviceManager
import slawomir.qstra.ble.bluetooth.bluetooth.BluetoothDeviceManagerImpl
import slawomir.qstra.ble.ui.activity.vm.ScannerViewModel
import slawomir.qstra.ble.ui.activity.vm.ScannerViewModelFactory
import slawomir.qstra.ble.utils.logger.Logger
import slawomir.qstra.ble.utils.logger.LoggerIml

fun appModule(context: Context) = Kodein.Module {

    bind<Logger>() with singleton { LoggerIml() }

    bind<BluetoothDeviceManager>() with singleton { BluetoothDeviceManagerImpl(context) }

    bind<ScannerViewModelFactory>() with singleton { ScannerViewModelFactory() }

    bind() from provider { ScannerViewModelFactory().create(ScannerViewModel::class.java) }
}