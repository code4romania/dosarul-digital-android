package ro.code4.casefile.ui.patient.addpatient

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.code4.casefile.data.helper.EnumConverters
import ro.code4.casefile.data.model.City
import ro.code4.casefile.data.model.County
import ro.code4.casefile.data.model.PatientDetails
import ro.code4.casefile.helper.SingleLiveEvent
import ro.code4.casefile.ui.base.BaseFormViewModel
import java.util.*

class AddPatientViewModel : BaseFormViewModel() {

    private val patientUiModelLiveData = MutableLiveData<AddPatientModel>()
    private val nameLiveData = MutableLiveData<String?>()

    private var addPatientModel = AddPatientModel()
    private val saveCompletedLiveData = SingleLiveEvent<AddUpdatePatientModel>()
    private val countiesLiveData = MutableLiveData<List<String>>()
    private val citiesLiveData = MutableLiveData<List<String>>()
    private var selectedPatientIdEdit: Int? = null
    private var selectedPatientIdAddFamilyMember: Int? = null

    fun patientUiModel(): LiveData<AddPatientModel> = patientUiModelLiveData
    fun nameUiModel(): LiveData<String?> = nameLiveData
    fun patientSaved(): SingleLiveEvent<AddUpdatePatientModel> = saveCompletedLiveData
    fun countiesInSpinner(): LiveData<List<String>> = countiesLiveData
    fun citiesInSpinner(): LiveData<List<String>> = citiesLiveData

    private var isUpdate = false
    private var counties = listOf<County>()
    private var cities = listOf<City>()

    init {
        disposables.add(repository.getCounties()
            .map {
                counties = it
                it.map { county -> county.name }
            }
            .subscribe {
                countiesLiveData.postValue(it)
            }
        )
    }

    @SuppressLint("CheckResult")
    fun initWith(editPatientId: Int?, addFamilyMemberForId: Int?) {
        editPatientId?.let {
            selectedPatientIdEdit = editPatientId
            disposables.add(
                repository.getPatientWithForms(it).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe { patient ->
                        patient?.let { patientDetails ->
                            setPatient(patientDetails)
                        }
                    })
        }
        addFamilyMemberForId?.let {
            selectedPatientIdAddFamilyMember = addFamilyMemberForId
        }
    }

    @SuppressLint("CheckResult")
    fun savePatient() {
        saveCompletedLiveData.postValue(addPatientModel.toEntity(selectedPatientIdAddFamilyMember))
    }

    fun nameChanged(name: String) {
        addPatientModel.name = name
    }

    fun dateOfBirthSelected(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        addPatientModel.dateOfBirth = calendar.time

        patientUiModelLiveData.postValue(addPatientModel)
    }

    fun maritalStatusSelected(maritalStatus: MaritalStatus) {
        addPatientModel.maritalStatus = maritalStatus.toUiMaritalStatus()
        patientUiModelLiveData.postValue(addPatientModel)
    }

    fun countySelected(position: Int) {
        newCountySelected(counties[position])
    }

    @SuppressLint("CheckResult")
    private fun newCountySelected(county: County?) {
        addPatientModel.city = null
        loadCitiesFor(county)
            ?.subscribe({
                citiesLiveData.postValue(it)
                patientUiModelLiveData.postValue(addPatientModel)
            }, {
                Log.e("COUNTIES", "SOME error here")
            })
    }

    private fun loadCitiesFor(county: County?): Observable<List<String>>? {
        addPatientModel.county = county
        return addPatientModel.county?.countyId?.let { newCounty ->
            repository.getCitiesByCounty(newCounty)
                .map {
                    cities = it
                    it.map { city -> city.name }
                }

        }
    }

    fun citySelected(position: Int) {
        addPatientModel.city = cities[position]
        patientUiModelLiveData.postValue(addPatientModel)
    }

    fun genderSelected(gender: Gender) {
        addPatientModel.gender = gender
        patientUiModelLiveData.postValue(addPatientModel)
    }

    private fun setPatient(patient: PatientDetails?) {
        if (patient != null) {
            isUpdate = true
            addPatientModel.name = patient.name
            addPatientModel.dateOfBirth = patient.birthDate
            addPatientModel.beneficiaryId = patient.beneficiaryId
            addPatientModel.dateOfBirth = patient.birthDate
            patient.countyId?.let { countyId ->
                repository.getCountyById(countyId).subscribe { county ->
                    addPatientModel.county = county
                    loadCitiesFor(county)
                        ?.take(1)
                        ?.subscribe({
                            citiesLiveData.postValue(it)
                            patient.cityId?.let { cityId ->
                                repository.getCityById(cityId).subscribe { city ->
                                    addPatientModel.city = city
                                    patientUiModelLiveData.postValue(addPatientModel)
                                }
                            }
                        }, {
                            Log.e("COUNTIES", "SOME error here")
                        })
                    patientUiModelLiveData.postValue(addPatientModel)
                }

            }
            addPatientModel.maritalStatus =
                EnumConverters.convertIntToMaritalStatus(patient.civilStatus).toUiMaritalStatus()
            addPatientModel.gender = EnumConverters.convertIntToGender(patient.gender)
//            addPatientModel.familyRelation = patient.familyMembers
        }

        patientUiModelLiveData.value = addPatientModel
        nameLiveData.value = patient?.name
    }
}


