package ro.code4.casefile.data.model

import com.google.gson.annotations.Expose
import ro.code4.casefile.data.model.answers.AnsweredQuestion
import java.util.*

class ResponseAnswerContainer {

    @Expose
    var formId: Int? = null

    @Expose
    var completionDate: Date? = null

    @Expose
    var answers: List<AnsweredQuestion>? = null
    override fun toString(): String {
        return "ResponseAnswerContainer(formId=$formId, completionDate=$completionDate, answers=$answers)"
    }


}
