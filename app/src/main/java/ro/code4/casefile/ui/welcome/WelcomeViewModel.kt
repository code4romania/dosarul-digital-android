package ro.code4.casefile.ui.welcome

import ro.code4.casefile.helper.SingleLiveEvent
import ro.code4.casefile.ui.base.BaseViewModel

class WelcomeViewModel : BaseViewModel() {
    private val nextLiveData = SingleLiveEvent<Void>()
    private val nextToMainLiveData = SingleLiveEvent<Void>()

    fun next(): SingleLiveEvent<Void> = nextLiveData
    fun nextToMain(): SingleLiveEvent<Void> = nextToMainLiveData
}
