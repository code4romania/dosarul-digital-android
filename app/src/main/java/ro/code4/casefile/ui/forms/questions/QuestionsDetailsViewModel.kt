package ro.code4.casefile.ui.forms.questions

import android.util.Log
import android.view.View
import io.reactivex.Single
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.DateQuestionListItem
import ro.code4.casefile.adapters.helper.DetailsQuestionListItem
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.adapters.helper.QuestionDetailsListItem
import ro.code4.casefile.data.model.Answer
import ro.code4.casefile.data.model.Note
import ro.code4.casefile.data.model.Question
import ro.code4.casefile.data.model.answers.AnsweredQuestion
import ro.code4.casefile.data.model.answers.SelectedAnswer
import ro.code4.casefile.data.pojo.AnsweredQuestionPOJO
import ro.code4.casefile.data.pojo.QuestionWithAnswers
import ro.code4.casefile.data.pojo.SectionWithQuestions
import ro.code4.casefile.helper.Constants.TYPE_DATE
import ro.code4.casefile.helper.Constants.TYPE_MULTI_CHOICE
import ro.code4.casefile.helper.Constants.TYPE_MULTI_CHOICE_DETAILS
import ro.code4.casefile.helper.Constants.TYPE_NUMBER
import ro.code4.casefile.helper.Constants.TYPE_SINGLE_CHOICE
import ro.code4.casefile.helper.Constants.TYPE_SINGLE_CHOICE_DETAILS
import ro.code4.casefile.helper.Constants.TYPE_TEXT
import ro.code4.casefile.ui.forms.model.AnswerUiModel
import ro.code4.casefile.ui.forms.model.QuestionDetailsUiModel

class QuestionsDetailsViewModel : BaseQuestionViewModel() {

    private val currentItemsList = mutableListOf<QuestionWithAnswers?>()

    override fun processList(
        formId: Int,
        beneficiaryId: Int,
        sections: List<SectionWithQuestions>,
        notes: List<Note>,
        answersForForm: List<AnsweredQuestionPOJO>
    ): List<ListItem> {
        currentItemsList.clear()
        val list = ArrayList<ListItem>()
        sections.forEach { sectionWithQuestion ->
            sectionWithQuestion.questions.forEach { questionWithAnswers ->
                var synced = false
                var savedLocally = false
                val hasNotes =
                    notes.any { it.beneficiaryId == beneficiaryId && it.formId == formId && it.questionId == questionWithAnswers.question.questionId }
                val notesSynced =
                    notes.none { it.beneficiaryId == beneficiaryId && it.formId == formId && it.questionId == questionWithAnswers.question.questionId && !it.synced }

                val answeredQuestion = answersForForm.find {
                    it.answeredQuestion.questionId == questionWithAnswers.question.questionId &&
                        it.answeredQuestion.beneficiaryId == selectedFormInfo.beneficiaryId &&
                        it.answeredQuestion.formId == selectedFormInfo.formId
                }
                answeredQuestion?.also { savedQuestion ->
                    savedLocally = savedQuestion.answeredQuestion.savedLocally
                    synced = savedQuestion.answeredQuestion.synced
                }

                questionWithAnswers.answers?.forEach { answer ->
                    val selectedAnswer =
                        answeredQuestion?.selectedAnswers?.find { it.optionId == answer.idOption }
                    if (selectedAnswer != null) {
                        answer.selected = true
                        if (answer.isFreeText) {
                            answer.value = selectedAnswer.value.orEmpty()
                        }
                    } else {
                        answer.selected = false
                    }
                }
                val uiModel = questionWithAnswers.toQuestionDetailsUiModel(
                    synced,
                    savedLocally,
                    hasNotes,
                    notesSynced
                )
                currentItemsList.add(questionWithAnswers)
                when (questionWithAnswers.question.questionType) {

                    TYPE_SINGLE_CHOICE, TYPE_SINGLE_CHOICE_DETAILS -> list.add(
                        DateQuestionListItem(uiModel)
                    )
                    TYPE_MULTI_CHOICE, TYPE_MULTI_CHOICE_DETAILS -> list.add(
                        DateQuestionListItem(uiModel)
                    )
                    TYPE_DATE -> list.add(DateQuestionListItem(uiModel))
                    TYPE_TEXT -> list.add(DetailsQuestionListItem(uiModel))
                    TYPE_NUMBER -> list.add(DetailsQuestionListItem(uiModel))
                }
            }
        }
        return list
    }

    val TAG = "QDVM"
    fun saveAnswer(
        listItem: QuestionDetailsListItem,
        position: Int,
        saveCompletedListener: (() -> Unit)? = null
    ) {
        val itemToSave = listItem.questionDetailsUiModel.toQuestionDetailsUiModel()
        if (itemToSave == currentItemsList[position]) {
            saveCompletedListener?.invoke()
            return
        }
        disposables.add(repository.getAnsweredQuestion(
            itemToSave.question.questionId,
            selectedFormInfo.beneficiaryId,
            selectedFormInfo.formId
        )
            .take(1)
            .onErrorReturn {
                listOf(
                    AnsweredQuestion(
                        itemToSave.question.questionId,
                        selectedFormInfo.beneficiaryId,
                        selectedFormInfo.formId
                    )
                )
            }.subscribe({ answeredQuestionList ->
                val answeredQuestion = if (answeredQuestionList.isNullOrEmpty()) {
                    AnsweredQuestion(
                        itemToSave.question.questionId,
                        selectedFormInfo.beneficiaryId,
                        selectedFormInfo.formId
                    )
                } else {
                    answeredQuestionList[0]
                }
                answeredQuestion.synced = false
                itemToSave.answers?.filter { it.selected }.also {

                    if (!it.isNullOrEmpty()) {
                        val list = it
                            .map { answer ->
                                SelectedAnswer(
                                    answer.idOption,
                                    if (answer.isFreeText) answer.value else null
                                )
                            }
                        Log.d(TAG, "saveAnswer: answeredQuestion=$answeredQuestion")
                        Log.d(TAG, "saveAnswer: selectedAnswer=$list")
                        val callToBeMade = if (itemToSave.question.questionId != 0) {
                            repository.deleteAnsweredQuestion(answeredQuestion)
                        } else {
                            Single.just(1)
                        }
                        callToBeMade.flatMap {
                            repository.saveAnsweredQuestion(answeredQuestion, list)
                        }.subscribe({
                            Log.d("COMPLETABLE", "The end")
                            saveCompletedListener?.invoke()
                        }, {
                            Log.e("ANSWERS", "WTF")
                            saveCompletedListener?.invoke()
                        })
                    }
                }
            }, { error ->
                Log.e("WUT", error.localizedMessage)
            })
        )
    }
}

private fun QuestionWithAnswers.toQuestionDetailsUiModel(
    synced: Boolean,
    savedLocally: Boolean,
    hasNotes: Boolean,
    notesSynced: Boolean
): QuestionDetailsUiModel {
    with(question) {
        val syncTextVisibility = if (savedLocally && !synced) View.VISIBLE else View.INVISIBLE
        val syncIconRes = when {
            synced -> R.drawable.ic_check_enabled
            savedLocally && !synced -> R.drawable.ic_check
            else -> 0
        }
        val addNoteTextRes =
            if (hasNotes) R.string.view_add_note_to_question else R.string.add_note_to_question

        return QuestionDetailsUiModel(
            questionId = questionId,
            questionCode = code ?: "",
            question = text,
            sectionId = sectionId,
            questionType = questionType,
            isQuestionSynced = synced,
            syncTextVisibility = syncTextVisibility,
            syncIconRes = syncIconRes,
            addNoteTextRes = addNoteTextRes,
            answers = answers?.map { it.toAnswerUiModel() } ?: listOf()
        )
    }
}

private fun QuestionDetailsUiModel.toQuestionDetailsUiModel(): QuestionWithAnswers {

    return QuestionWithAnswers(
        question = Question(questionId, question, questionCode, questionType, sectionId),
        answers = answers.map { it.toAnswerUiModel(questionId) }
    )
}

private fun Answer.toAnswerUiModel(): AnswerUiModel {
    return AnswerUiModel(idOption, text, isFreeText, value, selected)
}

private fun AnswerUiModel.toAnswerUiModel(questionId: Int): Answer {
    return Answer(idOption, text, isFreeText, questionId, isSelected, value)
}
