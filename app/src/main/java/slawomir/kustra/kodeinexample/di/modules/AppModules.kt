package slawomir.kustra.kodeinexample.di.modules

import android.content.Context
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import slawomir.kustra.kodeinexample.data.database.Database
import slawomir.kustra.kodeinexample.data.database.DatabaseImpl
import slawomir.kustra.kodeinexample.data.database.QuoteDao
import slawomir.kustra.kodeinexample.data.database.QuoteDaoImpl
import slawomir.kustra.kodeinexample.data.repository.QuoteRepository
import slawomir.kustra.kodeinexample.data.repository.QuoteRepositoryImpl
import slawomir.kustra.kodeinexample.ui.activity.QuotesViewModelFactory
import slawomir.kustra.kodeinexample.utils.logger.Logger
import slawomir.kustra.kodeinexample.utils.logger.LoggerIml

/*
Binding
 */
fun appModule(context: Context) = Kodein.Module {

    bind<Logger>() with singleton { LoggerIml() }

    bind<Database>() with singleton { DatabaseImpl() }

    bind<QuoteDao>() with singleton { instance<Database>().quoteDao }

    bind<QuoteRepository>() with singleton { QuoteRepositoryImpl(instance()) }

    /*
    provides -> new instance in opposite to singleton
     */
    bind() from provider { QuotesViewModelFactory(instance()) }
}