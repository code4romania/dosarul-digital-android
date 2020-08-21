package ro.code4.casefile.data.model.answers

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import ro.code4.casefile.data.model.Answer

@Entity(
    tableName = "selected_answer", indices = [Index(value = ["answeredQuestionId", "optionId"], unique = true)]
)
class SelectedAnswer() {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Expose
    var optionId: Int = -1

    @Expose
    var value: String? = null

    var answeredQuestionId: Int = 0


    constructor(
        optionId: Int,
        value: String? = null
    ) : this() {
        this.optionId = optionId
        this.value = value
    }

    override fun toString(): String {
        return "optionId=$optionId, value=$value"
    }
}
