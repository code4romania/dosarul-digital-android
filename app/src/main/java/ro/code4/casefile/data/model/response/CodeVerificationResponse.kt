package ro.code4.casefile.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CodeVerificationResponse(
    @Expose
    @SerializedName("succeeded")
    var succeeded: Boolean,

    @Expose
    @SerializedName("message")
    var message: String? = null
)

class CodeVerificationMessage(
    @Expose
    @SerializedName("message")
    var message: String? = null
)
