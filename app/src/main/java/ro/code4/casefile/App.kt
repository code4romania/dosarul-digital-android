package ro.code4.casefile

import android.app.Application
import android.content.Context
import android.util.Log
import com.sylversky.fontreplacer.FontReplacer
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ro.code4.casefile.data.AppDatabase
import ro.code4.casefile.helper.LocaleManager
import ro.code4.casefile.modules.*
import ro.code4.casefile.ui.base.BaseActivity
import java.io.IOException
import java.net.SocketException


class App : Application() {
    var currentActivity: BaseActivity<*>? = null

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, apiModule, dbModule, viewModelsModule, analyticsModule, firebaseModule, loginModule))
        }
        replaceFonts()
        AppDatabase.getDatabase(this)

    }

    private fun replaceFonts() {
        val replacer = FontReplacer.Build(applicationContext)
        replacer.setDefaultFont("fonts/SourceSansPro-Regular.ttf")
        replacer.setBoldFont("fonts/SourceSansPro-Bold.ttf")
        replacer.setLightFont("fonts/SourceSansPro-Light.ttf")
        replacer.setMediumFont("fonts/SourceSansPro-SemiBold.ttf")
        replacer.applyFont()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.wrapContext(base))
    }
}
