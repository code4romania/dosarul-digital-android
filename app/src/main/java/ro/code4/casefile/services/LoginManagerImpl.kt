package ro.code4.casefile.services

import io.reactivex.Observable
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Retrofit
import ro.code4.casefile.data.model.User
import ro.code4.casefile.data.model.response.LoginResponse

class LoginManagerImpl : KoinComponent, LoginManager {

    private val retrofit: Retrofit by inject()

    private val apiInterface: ApiInterface by inject()

    override fun login(user: User): Observable<LoginResponse> {
        return apiInterface.login(user)
    }
}
