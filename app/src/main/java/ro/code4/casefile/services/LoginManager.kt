package ro.code4.casefile.services

import io.reactivex.Observable

import ro.code4.casefile.data.model.User
import ro.code4.casefile.data.model.response.LoginResponse

interface LoginManager {
    fun login(user: User): Observable<LoginResponse>
}
