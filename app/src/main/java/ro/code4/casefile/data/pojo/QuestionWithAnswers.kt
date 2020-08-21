package ro.code4.casefile.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.casefile.data.model.Answer
import ro.code4.casefile.data.model.Question

class QuestionWithAnswers() {
    @Embedded
    lateinit var question: Question
    @Relation(parentColumn = "questionId", entityColumn = "questionId")
    var answers: List<Answer>? = null

    override fun equals(other: Any?): Boolean {
        return other is QuestionWithAnswers && question == other.question && answers == other.answers
    }

    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + answers.hashCode()
        return result
    }

    constructor(question: Question, answers: List<Answer>?): this() {
        this.question = question
        this.answers = answers
    }
}
