package ro.code4.casefile.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import org.parceler.Parcel

@Entity(
    tableName = "section", foreignKeys = [ForeignKey(
        entity = FormDetails::class,
        parentColumns = ["id"],
        childColumns = ["formId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
@Parcel(Parcel.Serialization.FIELD)
class Section() {

    @Expose
    var title: String? = null

    @PrimaryKey
    @Expose
    var sectionId: Int = 0

    @Expose
    var code: String? = null

    @Expose
    var description: String? = null

    @Expose
    @Ignore
    lateinit var questions: List<Question>

    var formId: Int = -1

    override fun equals(other: Any?): Boolean {
        if (other !is Section) {
            return false
        }

        return title == other.title && code == other.code && description == other.description
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Section(title=$title, sectionId=$sectionId, code=$code, description=$description, questions=$questions, formId=$formId)"
    }


    constructor(uniqueId: String, code: String, description: String, formId: Int): this() {
        this.title = uniqueId
        this.code = code
        this.description = description
        this.formId = formId
    }


}
