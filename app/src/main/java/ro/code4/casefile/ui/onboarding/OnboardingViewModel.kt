package ro.code4.casefile.ui.onboarding

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.OnboardingScreen
import ro.code4.casefile.helper.*
import ro.code4.casefile.ui.base.BaseViewModel
import java.util.*
import kotlin.collections.ArrayList

class OnboardingViewModel : BaseViewModel() {
    private val app: Application by inject()
    private val preferences: SharedPreferences by inject()
    private val languageChangedLiveData = SingleLiveEvent<Void>()
    private val onboardingLiveData = MutableLiveData<ArrayList<OnboardingScreen>>().apply {
        val screens = ArrayList<OnboardingScreen>()
        app.resources.getStringArray(R.array.languages)
        screens.add(
            OnboardingScreen(
                R.drawable.ic_onboarding_0,
                R.string.onboarding_title_0,
                R.string.onboarding_description_0
            )
        )
        screens.add(
            OnboardingScreen(
                R.drawable.ic_onboarding_1,
                R.string.onboarding_title_1,
                R.string.onboarding_description_1
            )
        )
        screens.add(
            OnboardingScreen(
                R.drawable.ic_onboarding_2,
                R.string.onboarding_title_2,
                R.string.onboarding_description_2
            )
        )
        screens.add(
            OnboardingScreen(
                R.drawable.ic_onboarding_3,
                R.string.onboarding_title_3,
                R.string.onboarding_description_3
            )
        )
        postValue(screens)
    }

    fun languageChanged(): LiveData<Void> = languageChangedLiveData
    fun onboarding(): LiveData<ArrayList<OnboardingScreen>> = onboardingLiveData
    fun onboardingCompleted() {
        preferences.completedOnboarding()
    }

    fun getSelectedLocale(): Locale = preferences.getLocaleCode().getLocale()
    fun changeLanguage(locale: Locale) {
        preferences.setLocaleCode(locale.encode())
        languageChangedLiveData.call()
    }
}

internal fun getLocales(codes: Array<String>): List<Locale> = codes.map { it.getLocale() }

internal fun Locale.encode() = "${this.language}_${this.country}"
