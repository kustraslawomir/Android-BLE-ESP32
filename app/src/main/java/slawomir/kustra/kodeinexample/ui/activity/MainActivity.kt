package slawomir.kustra.kodeinexample.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import slawomir.kustra.kodeinexample.R
import slawomir.kustra.kodeinexample.data.model.Quote
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private val quotesViewModelFactory: QuotesViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this, quotesViewModelFactory).get(QuotesViewModel::class.java)

        fab.setOnClickListener {
            viewModel.addQuote(Quote("Some fancy quote", "Johny  Bravo"))
        }

        viewModel.getQuotes().observe(this, Observer { quotes ->
            val stringBuilder = StringBuilder()
            quotes.forEach { quote ->
                stringBuilder.append(quote.toString())
            }
            quotesTextView.text = stringBuilder.toString()
        })
    }
}
