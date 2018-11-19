package slawomir.kustra.ble.utils.logger

interface Logger {


    /*
    Abstraction of Logger class. By abstract away code frame frameworks we make them testable by isolation.
     */
    fun log(tag: String, level: Level = Level.Verbose, message: String)

    fun log(message: String)

    sealed class Level {
        object Info : Level()
        object Debug : Level()
        object Warning : Level()
        object Error : Level()
        object Verbose : Level()
    }
}