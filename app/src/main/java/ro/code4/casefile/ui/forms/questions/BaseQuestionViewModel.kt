package ro.code4.casefile.ui.forms.questions

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.data.model.Note
import ro.code4.casefile.data.pojo.AnsweredQuestionPOJO
import ro.code4.casefile.data.pojo.SectionWithQuestions
import ro.code4.casefile.ui.base.BaseFormViewModel
import ro.code4.casefile.ui.forms.model.SelectedFormInfo

abstract class BaseQuestionViewModel : BaseFormViewModel() {

    private val questionsLiveData = MutableLiveData<List<ListItem>>()
    val syncVisibilityLiveData = MediatorLiveData<Int>()

    private val syncActivity = MutableLiveData<ro.code4.casefile.helper.Result<Int, Throwable>>()
    fun syncIndicator(): LiveData<ro.code4.casefile.helper.Result<Int, Throwable>> = syncActivity

    protected lateinit var selectedFormInfo: SelectedFormInfo

    fun questions(): LiveData<List<ListItem>> = questionsLiveData

    protected var questionsSub: Disposable? = null

    fun getQuestions(selectedFormInfo: SelectedFormInfo) {
//        val oldQs = questionsSub
//        if (oldQs != null && !oldQs.isDisposed) oldQs.dispose()
        val questionsSub = Observable.zip(
            repository.getSectionsWithQuestions(selectedFormInfo.formId),
            repository.getQuestionNotes(selectedFormInfo.beneficiaryId, selectedFormInfo.formId),
            repository.getAnswersForForm(selectedFormInfo.formId, selectedFormInfo.beneficiaryId),
            Function3<List<SectionWithQuestions>, List<Note>, List<AnsweredQuestionPOJO>, List<ListItem>> { a, b, c ->
                processList(selectedFormInfo.formId, selectedFormInfo.beneficiaryId, a, b, c)
            }
        ).subscribe {
            questionsLiveData.postValue(it)
        }
//        this.questionsSub = questionsSub
        disposables.add(questionsSub)
    }

    abstract fun processList(
        formId: Int,
        beneficiaryId: Int,
        sections: List<SectionWithQuestions>,
        notes: List<Note>,
        answersForForm: List<AnsweredQuestionPOJO>
    ): List<ListItem>

    fun setData(selectedFormInfo: SelectedFormInfo) {
        this.selectedFormInfo = selectedFormInfo
        getQuestions(selectedFormInfo)
        setTitle(selectedFormInfo.title)
        getSyncData(selectedFormInfo.formId)
    }

    private fun getSyncData(formId: Int) {
        // TODO For now we're not taking not-synced notes into consideration since can't be easily
        //  linked to a formId.
        //TODO double check this is actually fine
        // val notSyncedNotesCount = repository.getNotSyncedNotes()
        fun update(notSyncedQuestionsCount: Int) {
            Log.d("QVM", "notSyncedQuestionsCount=${notSyncedQuestionsCount}")
            syncVisibilityLiveData.value =
                if ((notSyncedQuestionsCount ?: 0) /*+ (notSyncedNotesCount.value ?: 0)*/ > 0)
                    View.VISIBLE else View.GONE
        }

        disposables.add(
            Observable.zip(repository.getNotSyncedQuestionsObservable(formId),
                repository.getNotSyncedNotes(),
                BiFunction<Int, Int, Int> { t1, t2 ->
                    t1 + t2
                }
            ).take(1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    val notSyncedQuestionsCount = it
                    update(notSyncedQuestionsCount)
                }, {})
        )
    }

    fun sync() {
        syncActivity.postValue(ro.code4.casefile.helper.Result.Loading)
        repository.syncData(selectedFormInfo).subscribe(
            {

            },
            {
                syncActivity.postValue(ro.code4.casefile.helper.Result.Failure(it))
                Log.e("SYNC", "ERROR ${it.localizedMessage.orEmpty()}")
            }, {
                getSyncData(selectedFormInfo.formId)
                syncActivity.postValue(ro.code4.casefile.helper.Result.Success(1))
            }
        )
    }
}
