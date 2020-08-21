package ro.code4.casefile.ui.login.codeverification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.casefile.data.model.response.CodeVerificationMessage
import ro.code4.casefile.helper.Result
import ro.code4.casefile.helper.logE
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.ui.base.BaseViewModel

private const val VERIFICATION_CODE_LENGTH = 4

class CodeVerificationViewModel : BaseViewModel() {

    private val repository: Repository by inject()

    private val codeLengthVerified: MutableLiveData<Boolean> = MutableLiveData(false)

    private val codeVerified: MutableLiveData<Result<Void, CodeVerificationException>> =
        MutableLiveData()
    private val codeResent: MutableLiveData<Result<Void, ResendCodeException>> = MutableLiveData()

    fun codeLengthVerified(): LiveData<Boolean> = codeLengthVerified
    fun codeVerified(): LiveData<Result<Void, CodeVerificationException>> = codeVerified
    fun codeResent(): LiveData<Result<Void, ResendCodeException>> = codeResent

    fun codeChanged(code: String) {
        codeLengthVerified.postValue(code.length == VERIFICATION_CODE_LENGTH)
    }

    fun resendCode() {
        codeResent.postValue(Result.Loading)
        disposables.add(
            repository.resendCode().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    when (response.success) {
                        true -> codeResent.postValue(Result.Success())
                        else -> codeResent.postValue(Result.Failure(ResendCodeException(response.message)))
                    }
                }, {
                    logE("ResendCodeError", it)
                })
        )
    }

    fun verifyCode(code: String) {
        codeVerified.postValue(Result.Loading)
        disposables.add(
            repository.verifyCode(code).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    when (response.succeeded) {
                        true -> {
                            repository.verifiedAuthentication()
                            codeVerified.postValue(Result.Success())
                        }
                        else -> codeVerified.postValue(
                            Result.Failure(
                                CodeVerificationException(
                                    Gson().fromJson(
                                        response.message,
                                        CodeVerificationMessage::class.java
                                    ).message
                                )
                            )
                        )
                    }
                }, {
                    logE("CodeVerificationError", it)
                })
        )
    }
}
