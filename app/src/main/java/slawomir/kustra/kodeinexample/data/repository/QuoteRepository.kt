package slawomir.kustra.kodeinexample.data.repository

import androidx.lifecycle.LiveData
import slawomir.kustra.kodeinexample.data.model.Quote

interface QuoteRepository{
    fun addQuote(quote: Quote)
    fun getQuotes(): LiveData<List<Quote>>
}