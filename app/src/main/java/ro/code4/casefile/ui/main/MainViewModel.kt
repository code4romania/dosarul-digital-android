package ro.code4.casefile.ui.main

import org.koin.core.inject
import ro.code4.casefile.helper.SingleLiveEvent
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.ui.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val onLogoutLiveData = SingleLiveEvent<Void>()

    fun onLogoutLiveData(): SingleLiveEvent<Void> = onLogoutLiveData

    fun logout() {
        repository.deleteToken()
        repository.resetAuthenticationVerification()
        repository.resetHasChangedPassword()
        repository.resetHasCompletedOnboarding()

        onLogoutLiveData.call()
    }

    fun notifyChangeRequested() {
    }
}
