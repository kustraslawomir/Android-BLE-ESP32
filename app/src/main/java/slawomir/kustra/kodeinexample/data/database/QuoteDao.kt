package slawomir.kustra.kodeinexample.data.database

import androidx.lifecycle.LiveData
import slawomir.kustra.kodeinexample.data.model.Quote

interface QuoteDao {
    fun addQuote(quote: Quote)
    fun getQuotes(): LiveData<List<Quote>>
}