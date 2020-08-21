package ro.code4.casefile.ui.guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ro.code4.casefile.BuildConfig
import ro.code4.casefile.ui.base.BaseViewModel
import java.net.URLEncoder

class GuideViewModel : BaseViewModel() {
    private val urlLiveData = MutableLiveData<String>().apply {
        value = "https://docs.google.com/gview?embedded=true&url=${URLEncoder.encode(
            BuildConfig.GUIDE_URL,
            "UTF-8"
        )}"
    }

    fun url(): LiveData<String> = urlLiveData
}
