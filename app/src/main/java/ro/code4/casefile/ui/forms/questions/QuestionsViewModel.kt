package ro.code4.casefile.ui.forms.questions

import android.view.View
import androidx.lifecycle.LiveData
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.adapters.helper.QuestionListItem
import ro.code4.casefile.adapters.helper.QuestionSectionListItem
import ro.code4.casefile.data.model.Note
import ro.code4.casefile.data.model.Question
import ro.code4.casefile.data.model.Section
import ro.code4.casefile.data.pojo.AnsweredQuestionPOJO
import ro.code4.casefile.data.pojo.SectionWithQuestions
import ro.code4.casefile.ui.forms.model.QuestionUiModel
import ro.code4.casefile.ui.forms.model.SectionUiModel

class QuestionsViewModel : BaseQuestionViewModel() {

    override fun processList(
        formId: Int,
        beneficiaryId: Int,
        sections: List<SectionWithQuestions>,
        notes: List<Note>,
        answersForForm: List<AnsweredQuestionPOJO>
    ): List<ListItem> {
        val list = ArrayList<ListItem>()
        sections.forEach { sectionWithQuestion ->
            list.add(QuestionSectionListItem(sectionWithQuestion.section.toSectionUiModel()))
            list.addAll(sectionWithQuestion.questions.map { questionWithAnswers ->
                var synced = false
                var savedLocally = false
                val hasNotes =
                    notes.any { it.beneficiaryId == beneficiaryId && it.formId == formId && it.questionId == questionWithAnswers.question.questionId }
                val notesSynced =
                    notes.none { it.beneficiaryId == beneficiaryId && it.formId == formId && it.questionId == questionWithAnswers.question.questionId && !it.synced }
                val answeredQuestion =
                    answersForForm.find { it.answeredQuestion.questionId == questionWithAnswers.question.questionId }
                answeredQuestion?.let {
                    synced = it.answeredQuestion.synced
                    savedLocally = it.answeredQuestion.savedLocally
                }

                QuestionListItem(
                    questionWithAnswers.question.questionId,
                    questionWithAnswers.question.toQuestionUiModel(
                        synced,
                        savedLocally,
                        hasNotes,
                        notesSynced
                    )
                )
            })
        }
        return list
    }

    fun syncVisibility(): LiveData<Int> = syncVisibilityLiveData
}

private fun Section.toSectionUiModel(): SectionUiModel {
    return SectionUiModel(
        title,
        description,
        if (description.isNullOrEmpty()) View.GONE else View.VISIBLE
    )
}

private fun Question.toQuestionUiModel(
    synced: Boolean,
    savedLocally: Boolean,
    hasNotes: Boolean,
    notesSynced: Boolean
): QuestionUiModel {
    val syncIconRes = when {
        savedLocally && synced -> R.drawable.ic_check_enabled
        savedLocally -> R.drawable.ic_check
        else -> 0
    }

    val noteIconRes = when {
        hasNotes && notesSynced -> R.drawable.ic_note_enabled
        hasNotes -> R.drawable.ic_note
        else -> 0
    }

    return QuestionUiModel(code, text, noteIconRes, syncIconRes)
}
