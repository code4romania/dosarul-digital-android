package ro.code4.casefile.services

import io.reactivex.Observable
import ro.code4.casefile.data.model.User
import ro.code4.casefile.data.model.response.LoginResponse

class MockLoginManager : LoginManager {
    override fun login(user: User): Observable<LoginResponse> {
        return Observable.fromCallable {
            LoginResponse().apply { this.accessToken = "dasfasf" } }
    }
}
