package ro.code4.casefile.ui.section.selection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.casefile.helper.SingleLiveEvent
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.ui.base.BaseViewModel
import ro.code4.casefile.ui.navigation.NavigationAddPatient
import ro.code4.casefile.ui.navigation.NavigationEvent
import ro.code4.casefile.ui.patient.addpatient.toUiMaritalStatus
import ro.code4.casefile.ui.patient.model.PatientUiModel

class PatientsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val patientsLiveData = MutableLiveData<List<PatientUiModel>>()

    private val selectedPatientDetailsLiveData = SingleLiveEvent<Int>()
    private val selectedPatientFormsLiveData = SingleLiveEvent<Int>()
    private val navigationEventLiveData = SingleLiveEvent<NavigationEvent>()

    fun patients(): LiveData<List<PatientUiModel>> = patientsLiveData
    fun patientDetails(): LiveData<Int> = selectedPatientDetailsLiveData
    fun fillPatientForm(): LiveData<Int> = selectedPatientFormsLiveData
    fun navigation(): LiveData<NavigationEvent> = navigationEventLiveData

    fun getPatients() {
        disposables.add(
            repository.getPatients().subscribeOn(Schedulers.io())
                .map {
                    it.map { patient ->
                        val patientUiModel = PatientUiModel()
                        patientUiModel.id = patient.beneficiaryId
                        patientUiModel.maritalStatus = patient.civilStatus.toUiMaritalStatus()
                        patientUiModel.name = patient.name
                        patientUiModel.age = patient.age
                        patientUiModel.county = patient.county
                        patientUiModel.city = patient.city
                        patientUiModel
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { patients ->
                    Log.d("PVM", "getPatients: ${patients.size}")
                    patientsLiveData.postValue(patients)
                }
        )
    }

    fun addPatient() {
        navigationEventLiveData.postValue(NavigationAddPatient())
    }

    fun patientDetails(patientId: Int) {
        selectedPatientDetailsLiveData.postValue(patientId)
    }

    fun fillPatientForm(patientId: Int) {
        selectedPatientFormsLiveData.postValue(patientId)
    }

    private fun onError(throwable: Throwable) {
        // TODO: Handle errors to show a specific message for each one
//        patientsLiveData.postValue(Result.Failure(throwable))
    }
}
