package ro.code4.casefile.ui.forms.model

import androidx.annotation.DrawableRes

data class QuestionUiModel(
    val questionCode: String?,
    val question: String,
    @DrawableRes val noteIconRes: Int,
    @DrawableRes val syncIconRes: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QuestionUiModel) return false

        if (questionCode != other.questionCode) return false
        if (question != other.question) return false
        if (noteIconRes != other.noteIconRes) return false
        if (syncIconRes != other.syncIconRes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = questionCode?.hashCode() ?: 0
        result = 31 * result + question.hashCode()
        result = 31 * result + noteIconRes
        result = 31 * result + syncIconRes
        return result
    }
}
