
package ro.code4.casefile.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ro.code4.casefile.data.model.Patient

class PatientsResponse {
    @Expose
    @SerializedName("data")
    var patients: List<Patient>? = null
}
