package ro.code4.casefile.ui.login.changepassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.casefile.helper.Result
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.ui.base.BaseViewModel
import java.util.regex.Pattern

private const val PASSWORD_REGEX_PATTERN =
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[%§\"&“|`´}{°><:.;#')(@_$!?*=^\\-]).{8,}$"

class ChangePasswordViewModel : BaseViewModel() {

    private val repository: Repository by inject()

    private val changePasswordButtonStateLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    private val passwordChangedLiveData: MutableLiveData<Result<Void, ChangePasswordException>> =
        MutableLiveData()
    private var newPassword: String? = null
    private var retypePassword: String? = null

    fun changePasswordButtonState(): LiveData<Boolean> = changePasswordButtonStateLiveData
    fun passwordChanged(): LiveData<Result<Void, ChangePasswordException>> = passwordChangedLiveData

    fun changePassword() {
        passwordChangedLiveData.postValue(Result.Loading)
        val isValidPassword = isValidPassword(newPassword)
        val arePasswordsEqual = newPassword.equals(retypePassword)

        if (!arePasswordsEqual) {
            passwordChangedLiveData.postValue(Result.Failure(ChangePasswordException.NOT_EQUAL_EXCEPTION))
        } else if (!isValidPassword) {
            passwordChangedLiveData.postValue(Result.Failure(ChangePasswordException.FORMAT_EXCEPTION))
        } else {
            repository.changedPassword()
            passwordChangedLiveData.postValue(Result.Success())
        }
    }

    private fun isValidPassword(newPassword: String?): Boolean {
        val pattern: Pattern = Pattern.compile(PASSWORD_REGEX_PATTERN)
        return newPassword?.let { pattern.matcher(newPassword).matches() } ?: run { false }
    }

    fun onNewPasswordChange(newPassword: String?) {
        this.newPassword = newPassword
        changePasswordButtonStateLiveData.postValue(
            !newPassword.isNullOrEmpty() && !retypePassword.isNullOrEmpty() &&
                newPassword.length == retypePassword?.length
        )
    }

    fun onRetypePasswordChange(retypePassword: String?) {
        this.retypePassword = retypePassword
        changePasswordButtonStateLiveData.postValue(
            !newPassword.isNullOrEmpty() && !retypePassword.isNullOrEmpty() &&
                newPassword?.length == retypePassword.length
        )
    }
}
