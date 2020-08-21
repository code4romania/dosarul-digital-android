package ro.code4.casefile.ui.splashscreen

import androidx.lifecycle.LiveData
import org.koin.core.inject
import ro.code4.casefile.helper.SingleLiveEvent
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.ui.base.BaseViewModel

class SplashScreenViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val loginLiveData = SingleLiveEvent<LoginStatus>()

    fun loginLiveData(): LiveData<LoginStatus> = loginLiveData

    init {
        checkLogin()
    }

    private fun checkLogin() {
        val isLoggedIn = repository.getToken() != null

        loginLiveData.postValue(
            LoginStatus(
                isLoggedIn,
                repository.hasChangedPassword(),
                repository.hasCompletedOnboarding()
            )
        )
    }

    data class LoginStatus(
        val isLoggedIn: Boolean,
        val changedPassword: Boolean,
        val onboardingCompleted: Boolean
    )
}
