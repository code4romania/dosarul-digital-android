package ro.code4.casefile.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.ui.base.BaseViewModel
import ro.code4.casefile.ui.navigation.NavigationChangePassword
import ro.code4.casefile.ui.navigation.NavigationCodeVerification
import ro.code4.casefile.ui.navigation.NavigationEvent
import ro.code4.casefile.ui.navigation.NavigationLogin
import ro.code4.casefile.ui.navigation.NavigationOnboarding
import ro.code4.casefile.ui.navigation.NavigationWelcome

class AuthenticationViewModel : BaseViewModel() {

    private val repository: Repository by inject()
    private val navigationLiveData = MutableLiveData<NavigationEvent>()
    private val progressDialogActionLiveData = MutableLiveData<ProgressDialogAction>()

    fun navigationLiveData(): LiveData<NavigationEvent> = navigationLiveData
    fun progressDialogAction(): LiveData<ProgressDialogAction> = progressDialogActionLiveData

    fun checkAuthenticationState() {
        val isLoggedIn = repository.getToken() != null
        val hasChangedPassword = repository.hasChangedPassword()
        if (isLoggedIn && !hasChangedPassword) {
            navigationLiveData.postValue(NavigationChangePassword())
        } else {
            navigationLiveData.postValue(NavigationLogin())
        }
    }

    fun checkAuthenticationCompleted() {
        if (!repository.isAuthenticationVerified()) {
            repository.deleteToken()
        }
    }

    fun successfulLogin() {
        navigationLiveData.postValue(NavigationCodeVerification())
    }

    fun codeVerified() {
        if (!repository.hasChangedPassword()) {
            navigationLiveData.postValue(NavigationChangePassword())
        } else if (repository.hasCompletedOnboarding()) {
            navigationLiveData.postValue(NavigationWelcome())
        } else {
            navigationLiveData.postValue(NavigationOnboarding())
        }
    }

    fun passwordChanged() {
        if (repository.hasCompletedOnboarding()) {
            navigationLiveData.postValue(NavigationWelcome())
        } else {
            navigationLiveData.postValue(NavigationOnboarding())
        }
    }

    fun showProgressDialog() {
        progressDialogActionLiveData.postValue(ProgressDialogAction.SHOW)
    }

    fun dismissProgressDialog() {
        progressDialogActionLiveData.postValue(ProgressDialogAction.DISMISS)
    }
}
