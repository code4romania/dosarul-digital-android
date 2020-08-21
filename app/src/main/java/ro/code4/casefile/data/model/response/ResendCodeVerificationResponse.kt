package ro.code4.casefile.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResendCodeVerificationResponse(
    @Expose
    @SerializedName("success")
    var success: Boolean,

    @Expose
    @SerializedName("message")
    var message: String? = null
)
