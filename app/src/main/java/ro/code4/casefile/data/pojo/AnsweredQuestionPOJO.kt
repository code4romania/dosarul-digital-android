package ro.code4.casefile.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.casefile.data.model.answers.AnsweredQuestion
import ro.code4.casefile.data.model.answers.SelectedAnswer

class AnsweredQuestionPOJO() {
    @Embedded
    lateinit var answeredQuestion: AnsweredQuestion
    @Relation(parentColumn = "id", entityColumn = "answeredQuestionId")
    lateinit var selectedAnswers: List<SelectedAnswer>

    constructor(answeredQuestion: AnsweredQuestion, selectedAnswers: List<SelectedAnswer>) : this() {
        this.answeredQuestion = answeredQuestion
        this.selectedAnswers = selectedAnswers
    }
}
