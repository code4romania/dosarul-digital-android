package ro.code4.casefile.data.model.answers

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(
    tableName = "answered_question", indices = [Index(value = ["questionId", "beneficiaryId", "formId"], unique = true)]
)
class AnsweredQuestion() {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Expose
    var formId: Int = -1

    @Expose
    var questionId: Int = -1

    @Expose
    var beneficiaryId: Int = -1

    @Ignore
    @Expose
    var options: List<SelectedAnswer>? = null

    var savedLocally = false
    var synced = false

    constructor(
        questionId: Int,
        beneficiaryId: Int,
        formId: Int
    ) : this() {
        this.questionId = questionId
        this.beneficiaryId = beneficiaryId
        this.formId = formId
    }

    override fun toString(): String {
        return "AnsweredQuestion(id=$id, formId=$formId, questionId=$questionId, beneficiaryId=$beneficiaryId, options=$options, savedLocally=$savedLocally, synced=$synced)"
    }


}
