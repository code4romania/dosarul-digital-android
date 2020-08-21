package ro.code4.casefile.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SyncResponse {
    @Expose
    @SerializedName("isCompletedSuccessfully")
    var isCompletedSuccessfully = false
}
