package ro.code4.casefile.data.model.response

import com.google.gson.annotations.Expose
import org.parceler.Parcel
import ro.code4.casefile.data.model.FormDetails

@Parcel(Parcel.Serialization.FIELD)
class VersionResponse {

    @Expose
    lateinit var formVersions: List<FormDetails>
}
