package ro.code4.casefile.adapters.helper

import androidx.annotation.StringRes
import ro.code4.casefile.data.model.Note
import ro.code4.casefile.ui.forms.model.FormItemUiModel
import ro.code4.casefile.ui.forms.model.QuestionDetailsUiModel
import ro.code4.casefile.ui.forms.model.QuestionUiModel
import ro.code4.casefile.ui.forms.model.SectionUiModel

sealed class ListItem
class QuestionSectionListItem(val sectionUiModel: SectionUiModel) : ListItem()
class QuestionListItem(val questionId: Int, val questionUiModel: QuestionUiModel) : ListItem()
open class QuestionDetailsListItem(open val questionDetailsUiModel: QuestionDetailsUiModel) :
    ListItem() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QuestionDetailsListItem) return false

        if (questionDetailsUiModel != other.questionDetailsUiModel) return false

        return true
    }

    override fun hashCode(): Int {
        return questionDetailsUiModel.hashCode()
    }
}

class SingleChoiceListItem(override val questionDetailsUiModel: QuestionDetailsUiModel) :
    QuestionDetailsListItem(questionDetailsUiModel) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SingleChoiceListItem) return false
        if (!super.equals(other)) return false

        if (questionDetailsUiModel != other.questionDetailsUiModel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + questionDetailsUiModel.hashCode()
        return result
    }
}

class MultiChoiceListItem(override val questionDetailsUiModel: QuestionDetailsUiModel) :
    QuestionDetailsListItem(questionDetailsUiModel) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MultiChoiceListItem) return false
        if (!super.equals(other)) return false

        if (questionDetailsUiModel != other.questionDetailsUiModel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + questionDetailsUiModel.hashCode()
        return result
    }
}

class DateQuestionListItem(override val questionDetailsUiModel: QuestionDetailsUiModel) :
    QuestionDetailsListItem(questionDetailsUiModel) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as DateQuestionListItem

        if (questionDetailsUiModel != other.questionDetailsUiModel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + questionDetailsUiModel.hashCode()
        return result
    }
}

class DetailsQuestionListItem(override val questionDetailsUiModel: QuestionDetailsUiModel) :
    QuestionDetailsListItem(questionDetailsUiModel) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as DetailsQuestionListItem

        if (questionDetailsUiModel != other.questionDetailsUiModel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + questionDetailsUiModel.hashCode()
        return result
    }
}

class NoteSectionListItem(@param:StringRes val titleResourceId: Int, vararg val formatArgs: Any) :
    ListItem()

class FormListItem(val formItemUiModel: FormItemUiModel) : ListItem()
class AddNoteListItem : ListItem()
class NoteListItem(val note: Note) : ListItem()


