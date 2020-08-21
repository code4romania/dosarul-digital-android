package ro.code4.casefile.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.casefile.R
import ro.code4.casefile.helper.replaceFragment
import ro.code4.casefile.helper.startActivityWithoutTrace
import ro.code4.casefile.ui.base.BaseAnalyticsActivity
import ro.code4.casefile.ui.login.changepassword.ChangePasswordFragment
import ro.code4.casefile.ui.login.codeverification.CodeVerificationFragment
import ro.code4.casefile.ui.navigation.NavigationChangePassword
import ro.code4.casefile.ui.navigation.NavigationCodeVerification
import ro.code4.casefile.ui.navigation.NavigationLogin
import ro.code4.casefile.ui.navigation.NavigationOnboarding
import ro.code4.casefile.ui.navigation.NavigationWelcome
import ro.code4.casefile.ui.onboarding.OnboardingActivity
import ro.code4.casefile.ui.welcome.WelcomeActivity
import ro.code4.casefile.widget.ProgressDialogFragment

class AuthenticationActivity : BaseAnalyticsActivity<AuthenticationViewModel>() {

    companion object {
        val TAG = AuthenticationActivity::class.java.simpleName
    }

    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }

    override val layout: Int
        get() = R.layout.activity_login
    override val screenName: Int
        get() = R.string.analytics_title_login

    override val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkAuthenticationState()

        viewModel.progressDialogAction().observe(this, Observer { action ->
            when (action) {
                ProgressDialogAction.SHOW -> progressDialog.showNow(
                    supportFragmentManager,
                    ProgressDialogFragment.TAG
                )
                ProgressDialogAction.DISMISS -> if (progressDialog.isResumed) progressDialog.dismiss()
            }
        })

        viewModel.navigationLiveData().observe(this, Observer { event ->
            when (event) {
                is NavigationLogin -> navigateTo(LoginFragment(), true)
                is NavigationCodeVerification -> navigateTo(CodeVerificationFragment())
                is NavigationChangePassword -> navigateTo(ChangePasswordFragment())
                is NavigationWelcome -> startActivityWithoutTrace(WelcomeActivity::class.java)
                is NavigationOnboarding -> startActivityWithoutTrace(OnboardingActivity::class.java)
            }
        })
    }

    override fun onDestroy() {
        viewModel.checkAuthenticationCompleted()
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroy()
    }

    private fun navigateTo(fragment: Fragment, isPrimary: Boolean = false) {
        supportFragmentManager.replaceFragment(
            R.id.content,
            fragment,
            isPrimaryNavigationFragment = isPrimary
        )
    }
}
