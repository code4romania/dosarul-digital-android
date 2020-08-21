package ro.code4.casefile.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import org.parceler.Parcel

@Entity(
    tableName = "question",
    foreignKeys = [ForeignKey(
        entity = Section::class,
        parentColumns = ["sectionId"],
        childColumns = ["sectionId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
@Parcel(Parcel.Serialization.FIELD)
class Question() {

    @PrimaryKey
    @Expose
    var questionId: Int = -1

    @Expose
    var formCode: String? = null

    @Expose
    var code: String? = null

    @Expose
    var sectionId: Int = -1

    // 1  single choice, 2 raspuns cu text, 3 multiple choice si raspuns liber 0 multiple choice
    @Expose
    var questionType: Int = 0

    @Expose
    lateinit var text: String

    @Expose
    var hint: String? = null

    @Expose
    @Ignore
    lateinit var optionsToQuestions: List<Answer>


    override fun equals(other: Any?): Boolean =
        other is Question && questionId == other.questionId && text == other.text && code == other.code &&
                questionType == other.questionType &&
                sectionId == other.sectionId

    override fun hashCode(): Int {
        var result = questionId
        result = 31 * result + text.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + questionType
        result = 31 * result + sectionId.hashCode()
        return result
    }

    constructor(id: Int): this() {
        this.questionId = id
    }
    constructor(id: Int, text: String, code: String, questionType: Int): this(id) {
        this.text = text
        this.code = code
        this.questionType = questionType
    }
    constructor(id: Int, text: String, code: String, questionType: Int, sectionId: Int): this(id, text, code, questionType) {
        this.sectionId = sectionId
    }


}
