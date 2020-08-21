package ro.code4.casefile.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import ro.code4.casefile.helper.SingleLiveEvent

abstract class BaseViewModel : ViewModel(), KoinComponent {
    val messageIdToastLiveData = SingleLiveEvent<String>()
    val disposables = CompositeDisposable()

    fun messageToast(): LiveData<String> = messageIdToastLiveData

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
