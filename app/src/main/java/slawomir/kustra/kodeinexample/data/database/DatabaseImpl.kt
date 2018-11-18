package slawomir.kustra.kodeinexample.data.database

class DatabaseImpl : Database {

    override val quoteDao: QuoteDao = QuoteDaoImpl()

}