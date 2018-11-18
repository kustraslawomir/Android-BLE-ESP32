package slawomir.kustra.kodeinexample.data.repository

import slawomir.kustra.kodeinexample.data.database.QuoteDao
import slawomir.kustra.kodeinexample.data.model.Quote

class QuoteRepositoryImpl(private val quoteDao: QuoteDao) : QuoteRepository {


    override fun addQuote(quote: Quote) {
        quoteDao.addQuote(quote)
    }

    override fun getQuotes() = quoteDao.getQuotes()


}