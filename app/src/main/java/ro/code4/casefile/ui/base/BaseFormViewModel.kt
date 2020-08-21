package ro.code4.casefile.ui.base

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.casefile.repositories.Repository

abstract class BaseFormViewModel : BaseViewModel() {
    val repository: Repository by inject()
    val preferences: SharedPreferences by inject()
    private val titleLiveData = MutableLiveData<String>()
    fun title(): LiveData<String> = titleLiveData
    fun setTitle(title: String) = titleLiveData.postValue(title)
}
