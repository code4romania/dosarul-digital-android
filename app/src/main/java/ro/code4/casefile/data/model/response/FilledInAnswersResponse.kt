
package ro.code4.casefile.data.model.response

import com.google.gson.annotations.Expose
import ro.code4.casefile.data.model.Answer

class FilledInAnswersResponse {
    @Expose
    var questionId: Int = -1

    @Expose
    var code: String? = null

    @Expose
    var formId: Int = -1

    // 1  single choice, 2 raspuns cu text, 3 multiple choice si raspuns liber 0 multiple choice
    @Expose
    var idQuestionType: Int = 0

    @Expose
    lateinit var text: String

    @Expose
    var answers: List<FilledInAnswer>? = null
}
