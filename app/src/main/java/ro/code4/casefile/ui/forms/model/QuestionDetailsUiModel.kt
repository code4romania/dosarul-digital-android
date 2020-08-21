package ro.code4.casefile.ui.forms.model

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class QuestionDetailsUiModel(
    val questionId: Int,
    val questionCode: String,
    val question: String,
    val questionType: Int,
    val sectionId: Int,
    var isQuestionSynced: Boolean,
    val syncTextVisibility: Int = View.INVISIBLE,
    @DrawableRes val syncIconRes: Int,
    @StringRes val addNoteTextRes: Int,
    var answers: List<AnswerUiModel>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuestionDetailsUiModel

        if (questionId != other.questionId) return false
        if (questionCode != other.questionCode) return false
        if (question != other.question) return false
        if (questionType != other.questionType) return false
        if (isQuestionSynced != other.isQuestionSynced) return false
        if (syncTextVisibility != other.syncTextVisibility) return false
        if (syncIconRes != other.syncIconRes) return false
        if (addNoteTextRes != other.addNoteTextRes) return false
        if (answers != other.answers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = questionId
        result = 31 * result + (questionCode.hashCode())
        result = 31 * result + question.hashCode()
        result = 31 * result + questionType
        result = 31 * result + isQuestionSynced.hashCode()
        result = 31 * result + syncTextVisibility
        result = 31 * result + syncIconRes
        result = 31 * result + addNoteTextRes
        result = 31 * result + answers.hashCode()
        return result
    }
}
