
package ro.code4.casefile.ui.login

import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Observable
import io.reactivex.Observable.fromPublisher
import ro.code4.casefile.BuildConfig
import ro.code4.casefile.helper.logW

class FirebaseAuthenticator {

//    val response = BehaviorSubject.create<FirebaseAuthenticatorResponse>()
    fun getFirebaseToken(): Observable<FirebaseAuthenticatorResponse> {
        return fromPublisher { subscriber ->
            try {
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                    val firebaseToken = it.result?.token
                    if (it.isSuccessful && firebaseToken != null) {
                        subscriber.onNext(FirebaseAuthenticatorResponse(firebaseToken, null))
                    } else {
                        logW("Failed to get firebase token!")
                        subscriber.onNext(FirebaseAuthenticatorResponse(null, it.exception?.message))
                    }
                }.addOnFailureListener {
                    subscriber.onNext(FirebaseAuthenticatorResponse(null, it.message))
                }
            } catch (exception: Exception) {
                // The google services are not active - just for development purposes
                if (BuildConfig.DEBUG && exception is IllegalStateException) {
                    subscriber.onNext(FirebaseAuthenticatorResponse("1234", null))
                } else {
                    logW("Exception while trying to get firebase token!")
                    subscriber.onNext(FirebaseAuthenticatorResponse(null, exception.message))
                }
            }
        }
    }
}
