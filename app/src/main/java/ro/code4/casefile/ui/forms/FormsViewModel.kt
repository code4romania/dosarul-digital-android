package ro.code4.casefile.ui.forms

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.FormListItem
import ro.code4.casefile.data.model.PatientDetails
import ro.code4.casefile.data.pojo.AnsweredQuestionPOJO
import ro.code4.casefile.data.pojo.FormWithSections
import ro.code4.casefile.helper.highlight
import ro.code4.casefile.ui.base.BaseFormViewModel
import ro.code4.casefile.ui.forms.model.FormItemUiModel
import ro.code4.casefile.ui.forms.model.SelectedFormInfo
import ro.code4.casefile.ui.login.ProgressDialogAction
import ro.code4.casefile.ui.navigation.*
import ro.code4.casefile.ui.patient.addpatient.AddUpdatePatientModel
import ro.code4.casefile.ui.patientbar.BeneficiaryBarUiModel

class FormsViewModel : BaseFormViewModel() {
    private val app: Application by inject()

    private val beneficiaryLiveData = MutableLiveData<BeneficiaryBarUiModel>()
    private val formsLiveData = MutableLiveData<List<FormListItem>>()
    private val navigationLiveData = MutableLiveData<NavigationEvent>()
    private val progressDialogActionLiveData = MutableLiveData<ProgressDialogAction>()
    private lateinit var selectedFormInfo: SelectedFormInfo
    private var patientId: Int = -1

    fun progressDialogAction(): LiveData<ProgressDialogAction> = progressDialogActionLiveData
    fun navigationLiveData(): LiveData<NavigationEvent> = navigationLiveData

    fun beneficiaryBarInfo(): LiveData<BeneficiaryBarUiModel> = beneficiaryLiveData
    fun forms(): LiveData<List<FormListItem>> = formsLiveData

    fun getBeneficiary(beneficiaryId: Int) {
        disposables.add(
            repository.getPatient(beneficiaryId).take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { beneficiary ->
                    beneficiary?.name?.let {
                        beneficiaryLiveData.postValue(
                            BeneficiaryBarUiModel(it, listener = ::navigateToPatientsList)
                        )
                    }
                }
        )
    }

    @SuppressLint("CheckResult")
    fun getForms(beneficiaryId: Int) {
        disposables.add(
            Observable.zip(
                repository.getAnswers(beneficiaryId),
                Observable.zip(
                    repository.getFormsWithQuestions(),
                    repository.getPatientWithForms(beneficiaryId),
                    BiFunction<List<FormWithSections>, PatientDetails, List<FormWithSections>> { t1, t2 ->
                        combineFormsWithPatient(t1, t2)
                    }
                ),
                BiFunction<List<AnsweredQuestionPOJO>, List<FormWithSections>, List<FormListItem>> { t1, t2 ->
                    processList(t1, t2)
                }
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    formsLiveData.postValue(it)
                }, {
                    Log.e("FORMS_ERROR", "FORMS ERROR")
                })
        )
    }

    fun showProgressDialog() {
        progressDialogActionLiveData.postValue(ProgressDialogAction.SHOW)
    }

    fun dismissProgressDialog() {
        progressDialogActionLiveData.postValue(ProgressDialogAction.DISMISS)
    }

    private fun combineFormsWithPatient(
        forms: List<FormWithSections>,
        patient: PatientDetails
    ): List<FormWithSections> {
        return forms.filter { formWithSections ->
            patient.forms?.any { patientForm ->
                patientForm.formId == formWithSections.form.id
            } ?: false

        }
    }

    private fun processList(
        answers: List<AnsweredQuestionPOJO>,
        forms: List<FormWithSections>
    ): List<FormListItem> {
        Log.e("ITEMS-FORMS", "${forms.size}")
        val items = ArrayList<FormListItem>()
        forms.forEach { formWithSections ->
            formWithSections.noAnsweredQuestions =
                answers.count { it.answeredQuestion.formId == formWithSections.form.id }
        }

        val formsList = forms.map {
            FormListItem(toFormItemInfo(it))
        }

        items.addAll(formsList)
        return items
    }

    private fun toFormItemInfo(formWithSections: FormWithSections): FormItemUiModel {
        val noQuestions =
            formWithSections.sections.fold(0, { acc, obj -> acc + obj.questions.size })

        val formProgress = formWithSections.noAnsweredQuestions
        val formProgressVisibility =
            if (formWithSections.noAnsweredQuestions == 0) View.GONE else View.VISIBLE

        val questionsAnswered = app.getString(
            R.string.no_answered_questions,
            formWithSections.noAnsweredQuestions,
            noQuestions
        )

        val formTitle = with(formWithSections.form) {
            app.highlight(
                description,
                app.getString(R.string.form_suffix, code)
            )
        }

        return FormItemUiModel(
            id = formWithSections.form.id,
            title = formWithSections.form.description,
            titleWithCode = formTitle,
            questionsAnswered = questionsAnswered,
            progress = formProgress,
            progressMax = noQuestions,
            progressVisibility = formProgressVisibility
        )
    }

    fun navigateToFillDate(selectedFormInfo: SelectedFormInfo) {
        navigationLiveData.postValue(NavigationFormFillDate(selectedFormInfo))
    }

    fun navigateToPatientsList() {
        navigationLiveData.postValue(NavigationPatientsList())
    }

    fun navigateToFormQuestions(selectedFormInfo: SelectedFormInfo) {
        this.selectedFormInfo = selectedFormInfo
        navigationLiveData.postValue(NavigationFormQuestions(selectedFormInfo))
    }

    fun navigateToQuestionDetails(questionId: Int) {
        selectedFormInfo.let { selectedFormInfo ->
            navigationLiveData.postValue(NavigationQuestionDetails(selectedFormInfo, questionId))
        }
    }

    fun navigateToPatientDetails(patientId: Int) {
        navigationLiveData.postValue(NavigationPatientDetails(patientId))
    }

    fun navigateToPatientForms(patientId: Int) {
        navigationLiveData.postValue(NavigationPatientDetailsForm(patientId))
    }

    fun navigateToPatientFormsSelection(patient: AddUpdatePatientModel) {
        navigationLiveData.postValue(NavigationPatientFormSelection(patient))
    }

    fun navigateToEditPatient(patientId: Int) {
        navigationLiveData.postValue(NavigationEditPatient(patientId))
    }

    fun navigateToAddPatient() {
        navigationLiveData.postValue(NavigationAddPatient())
    }

    fun navigateToAddFamilyMember(patientId: Int) {
        navigationLiveData.postValue(NavigationAddFamilyMember(patientId))
    }

    fun navigateToAddBeneficiaryNote(patientId: Int) {
        navigationLiveData.postValue((NavigationAddBeneficiaryNote(patientId)))
    }

    fun navigateToEditBeneficiaryNote(beneficiaryId: Int, noteId: Int) {
        navigationLiveData.postValue((NavigationEditBeneficiaryNote(beneficiaryId, noteId)))
    }

    fun selectedNotes(questionId: Int? = null, formId: Int? = null, beneficiaryId: Int) {
        navigationLiveData.postValue(NavigationQuestionNote(questionId, formId, beneficiaryId))
    }

    fun setPatient(patientId: Int) {
        this.patientId = patientId
        getForms(patientId)
    }

    private fun onError(throwable: Throwable) {
        // TODO Handle Errors
    }
}
