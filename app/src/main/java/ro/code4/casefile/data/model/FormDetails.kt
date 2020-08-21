package ro.code4.casefile.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(tableName = "form_details")
@Parcel(Parcel.Serialization.FIELD)
class FormDetails() {
    @PrimaryKey
    @Expose
    var id: Int = -1

    @Expose
    lateinit var code: String

    @Expose
    lateinit var description: String

    @Expose
    @SerializedName("currentVersion")
    var formVersion: Int = 0

    override fun equals(other: Any?): Boolean =
        other is FormDetails && id == other.id && code == other.code && formVersion == other.formVersion

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + code.hashCode()
        result = 31 * result + formVersion
        return result
    }

    constructor(id: Int, code: String, description: String, formVersion: Int): this() {
        this.id = id
        this.code = code
        this.description = description
        this.formVersion = formVersion
    }
}
