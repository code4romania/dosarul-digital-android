package ro.code4.casefile.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.casefile.data.model.Question
import ro.code4.casefile.data.model.Section

class SectionWithQuestions() {
    @Embedded
    lateinit var section: Section
    @Relation(parentColumn = "sectionId", entityColumn = "sectionId", entity = Question::class)
    lateinit var questions: List<QuestionWithAnswers>

    constructor(section: Section, questions: List<QuestionWithAnswers>): this() {
        this.section = section
        this.questions = questions
    }
}
