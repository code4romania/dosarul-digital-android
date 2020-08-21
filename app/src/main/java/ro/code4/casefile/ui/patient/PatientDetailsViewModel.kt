package ro.code4.casefile.ui.patient

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.casefile.R
import ro.code4.casefile.data.model.Patient
import ro.code4.casefile.data.model.PatientForm
import ro.code4.casefile.data.model.answers.AnsweredQuestion
import ro.code4.casefile.data.model.toUiModel
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.helper.Result
import ro.code4.casefile.services.ApiInterface
import ro.code4.casefile.ui.base.BaseFormViewModel
import ro.code4.casefile.ui.forms.model.SelectedFormInfo
import ro.code4.casefile.ui.patient.addpatient.toUiMaritalStatus
import ro.code4.casefile.ui.patient.model.*
import java.text.SimpleDateFormat
import java.util.*

class PatientDetailsViewModel : BaseFormViewModel() {

    private val patientModelLiveData = MutableLiveData<PatientUiModel>()
    private val patientFormHistoryLiveData = MutableLiveData<PatientFormHistoryUiModel>()
    private val patientFamilyMembersLiveData = MutableLiveData<PatientFamilyMembersUiModel>()
    private val patientNotesLiveData = MutableLiveData<PatientNotesUiModel>()

    private val sendRecordButtonLiveData = MutableLiveData<Result<Any, Any>>()

    private val patientCompletedForms = mutableListOf<PatientForm>()
    private val api: ApiInterface by inject()

    fun patientUiModel(): LiveData<PatientUiModel> = patientModelLiveData
    fun patientNotesUiModel(): LiveData<PatientNotesUiModel> = patientNotesLiveData
    fun patientFormHistoryUiModel(): LiveData<PatientFormHistoryUiModel> =
        patientFormHistoryLiveData

    fun patientFamilyMembersUiModel(): LiveData<PatientFamilyMembersUiModel> =
        patientFamilyMembersLiveData

    fun sendRecordButtonState(): LiveData<Result<Any, Any>> = sendRecordButtonLiveData

    fun getPatient(patientId: Int) {
        disposables.add(
            repository.getPatient(patientId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { patient ->
                    setPatient(patient)
                })
    }

    fun getPatientFormHistory(patientId: Int) {
        disposables.add(
            repository.getPatientForms(patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapSingle { patientForms ->
                    Single.merge(patientForms.map { patientForm ->
                        Single.fromObservable(
                            repository.getAnsweredQuestionsForForm(
                                patientForm.beneficiaryId,
                                patientForm.formId
                            ).take(1).map { answeredQuestions ->
                                PatientFormWithAnsweredQuestions(patientForm, answeredQuestions)
                            })
                    }).reduce(
                        mutableListOf<PatientFormWithAnsweredQuestions>(),
                        { acc, current ->
                            acc.add(current)
                            acc
                        }
                    )
                }.map {
                    it.filter { patientFormWithAnsweredQuestions ->
                        patientFormWithAnsweredQuestions.patientForm.totalQuestionsNo == patientFormWithAnsweredQuestions.answeredQuestions.size
                    }
                }.map { wholeList ->
                    wholeList.map { patientFormWithAnsweredQuestion ->
                        patientFormWithAnsweredQuestion.patientForm.isSynced =
                            (patientFormWithAnsweredQuestion.patientForm.totalQuestionsNo ==
                                patientFormWithAnsweredQuestion.answeredQuestions.filter { it.synced }.size)
                        patientFormWithAnsweredQuestion.patientForm
                    }
                }.subscribe({ completedForms ->
                    patientCompletedForms.clear()
                    patientCompletedForms.addAll(completedForms)

                    patientFormHistoryLiveData.postValue(
                        PatientFormHistoryUiModel(
                            formHistoryList = patientCompletedForms.map { patientForm ->
                                toPatientFormHistoryUiModel(patientForm)
                            },
                            formHistoryListVisibility = if (patientCompletedForms.isEmpty()) View.GONE else View.VISIBLE,
                            noFormHistoryLayoutVisibility = if (patientCompletedForms.isEmpty()) View.VISIBLE else View.GONE
                        )
                    )
                }, {
                    onError(it)
                })
        )
    }

    fun getSelectedFormInfo(beneficiaryId: Int, formUiModel: FormHistoryUiModel): SelectedFormInfo {
        return patientCompletedForms.firstOrNull { it.formId == formUiModel.formId }?.let {
            SelectedFormInfo(it.formId, it.description, beneficiaryId, it.completionDate)
        } ?: run {
            SelectedFormInfo(formUiModel.formId, formUiModel.formTitle, beneficiaryId, null)
        }
    }

    private fun onError(throwable: Throwable) {
        //TODO
    }

    private fun toPatientFormHistoryUiModel(patientForm: PatientForm): FormHistoryUiModel {
        val simpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT_SIMPLE, Locale.US)
        return FormHistoryUiModel(
            formId = patientForm.formId,
            formDate = patientForm.completionDate?.let { simpleDateFormat.format(it) } ?: "",
            formTitle = patientForm.description,
            syncIconRes = if (patientForm.isSynced) R.drawable.ic_check_enabled else R.drawable.ic_check
        )
    }

    fun getPatientFamilyMembers(patientId: Int) {
        disposables.add(repository.getPatientFamilyMembers(patientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.map { familyMember ->
                    FamilyMemberUiModel(familyMember.beneficiaryId, familyMember.name)
                }
            }
            .subscribe { familyMembers ->
                patientFamilyMembersLiveData.postValue(
                    PatientFamilyMembersUiModel(
                        familyMembers,
                        if (familyMembers.isNotEmpty()) View.VISIBLE else View.GONE,
                        if (familyMembers.isEmpty()) View.VISIBLE else View.GONE
                    )
                )
            })
    }

    fun getPatientNotes(beneficiaryId: Int) {
        disposables.add(
            repository.getBeneficiaryGenericNotes(beneficiaryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    patientNotesLiveData.postValue(
                        PatientNotesUiModel(
                            notes.map { it.toUiModel() },
                            if (notes.isNotEmpty()) View.VISIBLE else View.GONE,
                            if (notes.isEmpty()) View.VISIBLE else View.GONE
                        )
                    )
                })
    }

    private fun setPatient(patient: Patient?) {
        val value = patient ?: return
        val patientUiModel = PatientUiModel()
        patientUiModel.maritalStatus = patient.civilStatus.toUiMaritalStatus()
        patientUiModel.name = patient.name
        patientUiModel.age = patient.age
        patientUiModel.id = value.beneficiaryId
        patientUiModel.county = value.county
        patientUiModel.city = value.city

        patientModelLiveData.postValue(patientUiModel)
    }

    fun sendRecord(beneficiaryId: Int) {
        sendRecordButtonLiveData.postValue(Result.Loading)
       disposables.add( api.sendFile(beneficiaryId)
           .subscribeOn(Schedulers.io())
            .subscribe (
                {
                    sendRecordButtonLiveData.postValue(Result.Success())
                }, {
                    sendRecordButtonLiveData.postValue(Result.Failure(it))
                }
            )
       )
    }
}

data class PatientFormWithAnsweredQuestions(
    val patientForm: PatientForm,
    val answeredQuestions: List<AnsweredQuestion>
)
