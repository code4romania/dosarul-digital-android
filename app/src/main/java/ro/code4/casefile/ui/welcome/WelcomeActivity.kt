package ro.code4.casefile.ui.welcome

import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_polling_station.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.casefile.R
import ro.code4.casefile.helper.replaceFragment
import ro.code4.casefile.helper.startActivityWithoutTrace
import ro.code4.casefile.ui.base.BaseActivity
import ro.code4.casefile.ui.main.MainActivity

class WelcomeActivity : BaseActivity<WelcomeViewModel>() {
    override val layout: Int
        get() = R.layout.activity_polling_station

    override val viewModel: WelcomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        viewModel.nextToMain().observe(this, Observer {
            startActivityWithoutTrace(MainActivity::class.java)
        })

        replaceFragment(R.id.container,
            WelcomeFragment()
        )
    }
}
