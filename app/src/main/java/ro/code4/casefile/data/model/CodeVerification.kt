package ro.code4.casefile.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CodeVerification(
    @Expose
    @SerializedName("token")
    var token: String
)
