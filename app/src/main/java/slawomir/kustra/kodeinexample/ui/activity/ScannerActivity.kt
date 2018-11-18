package slawomir.kustra.kodeinexample.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import slawomir.kustra.kodeinexample.R
import slawomir.kustra.kodeinexample.ui.activity.vm.ScannerViewModel
import slawomir.kustra.kodeinexample.ui.activity.vm.ScannerViewModelFactory
import slawomir.kustra.kodeinexample.utils.logger.Logger

class ScannerActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private val logger by instance<Logger>()

    private val scannerViewModelFactory: ScannerViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this, scannerViewModelFactory).get(ScannerViewModel::class.java)

        fab.setOnClickListener {
            logger.log("scanner", Logger.Level.Verbose, "fab clicked")
        }
    }
}
