package ro.code4.casefile.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User(
    @field:Expose
    @SerializedName("email")
    var email: String,
    @field:Expose
    @SerializedName("password")
    var password: String
)
