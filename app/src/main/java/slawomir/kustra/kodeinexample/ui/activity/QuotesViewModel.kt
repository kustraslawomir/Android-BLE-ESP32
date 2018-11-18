package slawomir.kustra.kodeinexample.ui.activity

import androidx.lifecycle.ViewModel
import slawomir.kustra.kodeinexample.data.model.Quote
import slawomir.kustra.kodeinexample.data.repository.QuoteRepository

class QuotesViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    fun addQuote(quote: Quote) = quoteRepository.addQuote(quote)

    fun getQuotes() = quoteRepository.getQuotes()
}