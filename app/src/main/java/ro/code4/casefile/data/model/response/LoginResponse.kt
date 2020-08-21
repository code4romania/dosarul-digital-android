package ro.code4.casefile.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {
    @Expose
    @SerializedName("access_token")
    lateinit var accessToken: String

    @SerializedName("expires_in")
    var expiresIn: Long = 0

    @SerializedName("first_login")
    var isFirstLogin: Boolean = false

    @Expose
    @SerializedName("phone_verification_request")
    var phoneVerificationRequest: String? = null

}
