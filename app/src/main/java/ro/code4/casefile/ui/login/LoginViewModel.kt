package ro.code4.casefile.ui.login

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.casefile.analytics.Event
import ro.code4.casefile.data.model.User
import ro.code4.casefile.data.model.response.LoginResponse
import ro.code4.casefile.helper.AppExceptions
import ro.code4.casefile.helper.Result
import ro.code4.casefile.helper.SingleLiveEvent
import ro.code4.casefile.helper.isValidEmail
import ro.code4.casefile.helper.logD
import ro.code4.casefile.helper.logE
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.services.LoginManager
import ro.code4.casefile.ui.base.BaseViewModel

class LoginViewModel : BaseViewModel() {

    private val loginManager: LoginManager by inject()
    private val repository: Repository by inject()
    private val firebaseAnalytics: FirebaseAnalytics by inject()

    private val loginButtonState = MutableLiveData(false)
    private val loginLiveData = SingleLiveEvent<Result<Class<*>, AppExceptions>>()
    private var email: String? = null
    private var password: String? = null

    fun loginButtonState(): LiveData<Boolean> = loginButtonState
    fun loggedIn(): LiveData<Result<Class<*>, AppExceptions>> = loginLiveData

    fun onEmailChange(email: String?) {
        this.email = email
        loginButtonState.postValue(!email.isNullOrEmpty() && !password.isNullOrEmpty())
    }

    fun onPasswordChange(password: String?) {
        this.password = password
        loginButtonState.postValue(!email.isNullOrEmpty() && !password.isNullOrEmpty())
    }

    @SuppressLint("CheckResult")
    fun login(email: String, password: String) {
        loginButtonState.postValue(false)
        loginLiveData.postValue(Result.Loading)

        if (!email.isValidEmail()) {
            onError(AppExceptions.INVALID_EMAIL)
            return
        }
        actualLogin(email, password)
    }

    private fun actualLogin(phone: String, password: String) {
        logD("login: $phone : $password -> $password")
        disposables.add(
            loginManager.login(User(phone, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ loginResponse ->
                    logD("Login successful! Token received!")
                    logD("actualLogin: ${loginResponse.phoneVerificationRequest}")
                    onSuccessfulLogin(loginResponse)
                }, { throwable ->
                    firebaseAnalytics.logEvent(Event.LOGIN_FAILED.title, null)
                    logE("Login failed!", throwable)
                    onError(AppExceptions.GENERIC_ERROR)
                })
        )
    }

    @SuppressLint("CheckResult")
    private fun onSuccessfulLogin(loginResponse: LoginResponse) {
        logD("onSuccessfulLogin")
        repository.saveToken(loginResponse.accessToken)
        if (!loginResponse.isFirstLogin) {
            repository.changedPassword()
        }
        loginLiveData.postValue(Result.Success())
    }

    private fun onError(error: AppExceptions) {
        logE("onError $error")
        loginButtonState.postValue(true)
        loginLiveData.postValue(Result.Failure(error))
    }
}
