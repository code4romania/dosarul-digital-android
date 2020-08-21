package ro.code4.casefile.ui.forms.selection

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.casefile.data.model.FormDetails
import ro.code4.casefile.data.model.Patient
import ro.code4.casefile.helper.Result
import ro.code4.casefile.data.model.PatientDetails
import ro.code4.casefile.helper.highlight
import ro.code4.casefile.ui.base.BaseFormViewModel
import ro.code4.casefile.ui.forms.model.FormSelectionItemUiModel
import ro.code4.casefile.ui.patient.addpatient.AddUpdatePatientModel

class FormsSelectionViewModel : BaseFormViewModel() {
    private val app: Application by inject()
    private val formsSelectionLiveData =
        MutableLiveData<Result<List<FormSelectionItemUiModel>, Throwable>>()
    private val continueButtonLiveData = MutableLiveData(false)
    private val savedSelectionLiveData = MutableLiveData<Result<Void, Throwable>>()
    fun forms(): LiveData<Result<List<FormSelectionItemUiModel>, Throwable>> =
        formsSelectionLiveData

    private val formsList = mutableListOf<FormSelectionItemUiModel>()

    private val newAllocatedFormsIds = mutableSetOf<Int>()
    private val dealocatedFormsIds = mutableSetOf<Int>()
    private val alreadyAllocatedFormsIds = mutableSetOf<Int>()

    fun savedSelection(): LiveData<Result<Void, Throwable>> = savedSelectionLiveData

    private lateinit var patient: AddUpdatePatientModel

    @SuppressLint("CheckResult")
    private fun getForms(patientId: Int?) {
        formsSelectionLiveData.postValue(Result.Loading)
        disposables.add(
            repository.getLocalForms().take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { formDetails ->
                    if (patientId != null) {
                        repository.getPatientWithForms(patientId).take(1).map { patient ->
                            formDetails.map { form ->
                                val result = getFormSelectionListItem(form)
                                result.isSelected =
                                    patient.forms?.any { it.formId == form.id } ?: false
                                if (result.isSelected) alreadyAllocatedFormsIds.add(form.id)
                                result
                            }
                        }
                    } else {
                        Observable.just(formDetails.map { form ->
                            getFormSelectionListItem(form)
                        })
                    }
                }
                .subscribe({ formsSelectionItems ->
                    formsList.clear()
                    formsList.addAll(formsSelectionItems)
                    continueButtonLiveData.postValue(formsList.any { it.isSelected })
                    formsSelectionLiveData.postValue(Result.Success(formsList))
                }, {
                    formsSelectionLiveData.postValue(Result.Failure(it))
                })
        )
    }

    fun continueButtonState(): LiveData<Boolean> = continueButtonLiveData

    fun selectForm(formId: Int, isSelected: Boolean) {
        if (isSelected && !alreadyAllocatedFormsIds.contains(formId)) newAllocatedFormsIds.add(
            formId
        )
        if (!isSelected && alreadyAllocatedFormsIds.contains(formId)) dealocatedFormsIds.add(formId)

        if (isSelected && alreadyAllocatedFormsIds.contains(formId)) newAllocatedFormsIds.remove(
            formId
        )
        if (!isSelected && !alreadyAllocatedFormsIds.contains(formId)) dealocatedFormsIds.remove(
            formId
        )

        continueButtonLiveData.postValue(newAllocatedFormsIds.isNotEmpty() || dealocatedFormsIds.isNotEmpty())
    }

    fun savePatient() {
        savedSelectionLiveData.postValue(Result.Loading)

        val callToBeMade: Single<List<Patient>>
        if (patient.isEdit()) {
            patient.newAllocatedFormsIds =
                if (newAllocatedFormsIds.isNotEmpty()) newAllocatedFormsIds.toList() else null
            patient.dealocatedFormsIds =
                if (dealocatedFormsIds.isNotEmpty()) dealocatedFormsIds.toList() else null

            callToBeMade = repository.updatePatientDetails(patient).map {
                patient.beneficiaryId
            }.flatMap {
                repository.getPatientsFromServer()
            }
        } else {
            patient.formsIds = newAllocatedFormsIds.toList()
            callToBeMade = repository.savePatientDetails(patient)
                .flatMap {
                    repository.getPatientsFromServer()
                }
        }

        disposables.add(callToBeMade
            .flatMap {
                repository.getPatientsFromServer()
            }.flatMap {
                Single.merge(it.map { remotePatient ->
                    repository.getPatientDetailsFromServer(remotePatient.beneficiaryId)
                }).reduce(mutableListOf<PatientDetails>(), { acc, newElement ->
                    acc.add(newElement)
                    acc
                })
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("UPDATE_PATIENT", "Updated")
                savedSelectionLiveData.postValue(Result.Success())
            }, {
                Log.e("UPDATE_PATIENT", it.localizedMessage.orEmpty())
                savedSelectionLiveData.postValue(Result.Failure(it))
            })
        )
    }

    private fun getFormSelectionListItem(formDetails: FormDetails): FormSelectionItemUiModel {
        val formTitle = with(formDetails) {
            app.highlight(
                description,
                app.getString(ro.code4.casefile.R.string.form_suffix, code)
            )
        }.toString()

        return FormSelectionItemUiModel(formDetails.id, formTitle)
    }

    fun setPatient(patientModel: AddUpdatePatientModel) {
        this.patient = patientModel
        getForms(patient.beneficiaryId)
    }
}

fun AddUpdatePatientModel.isEdit(): Boolean {
    return beneficiaryId != null
}
