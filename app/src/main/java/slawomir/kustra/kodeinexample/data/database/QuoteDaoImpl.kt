package slawomir.kustra.kodeinexample.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import slawomir.kustra.kodeinexample.data.model.Quote

class QuoteDaoImpl : QuoteDao {

    private val quotes = mutableListOf<Quote>()
    private val quotesLiveData = MutableLiveData<List<Quote>>()

    init {
        quotesLiveData.value = quotes
    }

    override fun addQuote(quote: Quote) {
        quotes.add(quote)
        quotesLiveData.value = quotes
    }

    override fun getQuotes(): LiveData<List<Quote>> = quotesLiveData
}
