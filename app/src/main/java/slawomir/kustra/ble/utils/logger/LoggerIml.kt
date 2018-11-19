package slawomir.kustra.ble.utils.logger

import android.util.Log

class LoggerIml : Logger {

    override fun log(message: String) {
        log("logger: ", Logger.Level.Debug, message)
    }

    override fun log(tag: String, level: Logger.Level, message: String) {
        when (level) {
            Logger.Level.Info -> Log.i(tag, message)
            Logger.Level.Debug -> Log.d(tag, message)
            Logger.Level.Warning -> Log.w(tag, message)
            Logger.Level.Error -> Log.e(tag, message)
            Logger.Level.Verbose -> Log.v(tag, message)
        }
    }
}