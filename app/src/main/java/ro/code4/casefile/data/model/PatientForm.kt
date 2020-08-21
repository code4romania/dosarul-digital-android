package ro.code4.casefile.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel
import java.util.*

@Entity(tableName = "patient_forms")
@Parcel(Parcel.Serialization.FIELD)
class PatientForm() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Expose
    var formId: Int = -1

    @Expose
    lateinit var code: String

    @Expose
    var beneficiaryId: Int = 0

    @Expose
    var completionDate: Date? = null

    @Expose
    lateinit var description: String

    @Expose
    var totalQuestionsNo: Int = 0

    @Expose
    var questionsAnsweredNo: Int = 0

    @Ignore
    var isSynced: Boolean = false


    override fun equals(other: Any?): Boolean =
        other is PatientForm && id == other.id && code == other.code

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + code.hashCode()
        result = 31 * result
        return result
    }

    constructor(id: Int, code: String, description: String, formVersion: Int): this() {
        this.id = id
        this.code = code
        this.description = description
    }
}
