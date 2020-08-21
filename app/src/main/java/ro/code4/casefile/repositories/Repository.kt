package ro.code4.casefile.repositories

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import ro.code4.casefile.data.AppDatabase
import ro.code4.casefile.data.model.*
import ro.code4.casefile.data.model.answers.AnsweredQuestion
import ro.code4.casefile.data.model.answers.SelectedAnswer
import ro.code4.casefile.data.model.response.CodeVerificationResponse
import ro.code4.casefile.data.model.response.FilledInAnswersResponse
import ro.code4.casefile.data.model.response.ResendCodeVerificationResponse
import ro.code4.casefile.data.pojo.AnsweredQuestionPOJO
import ro.code4.casefile.data.pojo.FormWithSections
import ro.code4.casefile.data.pojo.SectionWithQuestions
import ro.code4.casefile.helper.*
import ro.code4.casefile.services.ApiInterface
import ro.code4.casefile.ui.forms.model.SelectedFormInfo
import ro.code4.casefile.ui.patient.addpatient.AddUpdatePatientModel
import java.io.File
import java.util.*

open class Repository : KoinComponent {

    companion object {
        @JvmStatic
        val TAG = Repository::class.java.simpleName
    }

    private val db: AppDatabase by inject()

    private val apiInterface: ApiInterface by inject()

    private val sharedPreferences: SharedPreferences by inject()

    private var syncInProgress = false

    open fun getPatients(): Observable<List<Patient>> {
        return db.patientsDao().getPatients()
    }

    fun getPatientsFromServer(): Single<List<Patient>> {
        return apiInterface.getPatients()
            .subscribeOn(Schedulers.io())
            .map {
                it.patients
            }.onErrorReturn {
                listOf()
            }.flatMap { patients ->
                savePatients(patients).map {
                    patients
                }
            }
    }

    open fun getPatient(patientId: Int): Observable<Patient> {
        return db.patientsDao().getPatient(patientId).toObservable()
    }

    fun getPatientFamilyMembers(patientId: Int): Observable<List<PatientDetailsFamilyMember>> {
        return db.patientsDao().getPatientFamilyMembers(patientId)
    }

    private fun savePatients(patients: List<Patient>): Single<List<Long>> {
        return db.patientsDao().save(patients)
    }

    fun savePatientDetails(patient: AddUpdatePatientModel): Single<Int> {
        return apiInterface.savePatientDetails(patient)
            .subscribeOn(Schedulers.io())
    }

    fun updatePatientDetails(patient: AddUpdatePatientModel): Single<Boolean> {
        return apiInterface.updatePatientDetails(patient)
            .subscribeOn(Schedulers.io())
    }

    fun getPatientDetailsFromServer(beneficiaryId: Int): Single<PatientDetails> {
        return apiInterface.getPatientDetails(beneficiaryId)
            .subscribeOn(Schedulers.io())
            .flatMap { patient ->
                savePatientDetailsLocally(patient).map { patient }
            }
    }

    private fun savePatientDetailsLocally(patient: PatientDetails): Single<List<Long>> {
        return db.patientDetailsDao().save(listOf(patient))
            .subscribeOn(Schedulers.io())
            .map {
                patient.forms?.map {
                    it.beneficiaryId = patient.beneficiaryId
                    it
                }
                patient
            }
            .flatMap {
                val forms = it.forms
                val members = it.familyMembers
                return@flatMap if (forms != null) {
                    removePatientForms(patient.beneficiaryId)
                    savePatientForms(forms)
                } else {
                    Single.just(listOf())
                }.flatMap {
                    removePatientFamilyMembers(patient.beneficiaryId)
                    if (members != null) {
                        members.map {
                            it.isFamilyOfBeneficiaryId = patient.beneficiaryId
                        }
                        savePatientFamilyMembers(members)
                    } else {
                        Single.just(listOf())
                    }
                }
            }
    }

    fun removeBeneficiary(beneficiaryId: Int) {
        db.cleanupDao().removeBeneficiary(beneficiaryId)
    }

    private fun removePatientForms(beneficiaryId: Int): Int {
        return db.cleanupDao().removePatientForms(beneficiaryId)
    }

    public fun savePatientForm(form: PatientForm): Single<Long> {
        return db.formDetailsDao().savePatientForm(form).subscribeOn(Schedulers.io())
    }

    private fun savePatientForms(forms: List<PatientForm>): Single<List<Long>> {
        return db.formDetailsDao().savePatientForm(forms).subscribeOn(Schedulers.io())
    }

    private fun removePatientFamilyMembers(beneficiaryId: Int): Int {
        return db.cleanupDao().removePatientFamilyMembers(beneficiaryId)
    }

    private fun savePatientFamilyMembers(members: List<PatientDetailsFamilyMember>): Single<List<Long>> {
        return db.formDetailsDao().savePatientFamilyMembers(members).subscribeOn(Schedulers.io())
    }

    fun getPatientForms(patientId: Int): Observable<List<PatientForm>> {
        return db.formDetailsDao().getPatientForms(patientId)
    }

    fun getPatientWithForms(patientId: Int): Observable<PatientDetails> {
        return db.patientDetailsDao().getPatientDetails(patientId).toObservable()
            .subscribeOn(Schedulers.io())
            .flatMap {
                db.formDetailsDao().getPatientForms(patientId)
                    .map { patientForms ->
                        val result = it
                        result.forms = patientForms
                        result
                    }
            }
    }

    fun getRemoteAnswers(
        beneficiaryId: Int,
        formId: Int
    ): Single<List<FilledInAnswersResponse>> {
        return apiInterface.getRemoteAnswers(beneficiaryId, formId)
    }

    open fun getAnswers(
        beneficiaryId: Int
    ): Observable<List<AnsweredQuestionPOJO>> {
        return db.formDetailsDao().getAnswersFor(beneficiaryId)
    }

    open fun getFormsWithQuestions(): Observable<List<FormWithSections>> {
        return db.formDetailsDao().getFormsWithSections()
    }

    open fun getSectionsWithQuestions(formId: Int): Observable<List<SectionWithQuestions>> {
        return db.formDetailsDao().getSectionsWithQuestions(formId)
    }

    private fun getRemoteForms(): Observable<List<FormDetails>> {
        return apiInterface.getForms().map { it.formVersions }
    }

    fun getLocalForms(): Observable<List<FormDetails>> {
        return db.formDetailsDao().getAllForms()
    }

    open fun getForms(): Observable<List<FormDetails>> {
        val observableDb = db.formDetailsDao().getAllForms()

        val observableApi = getRemoteForms()

        return Observable.zip(
            observableDb.onErrorReturn { null },
            observableApi.onErrorReturn { null },
            BiFunction<List<FormDetails>?, List<FormDetails>, List<FormDetails>> { dbFormDetails, response ->
                processFormDetailsData(dbFormDetails, response)
                response
            })
    }

    @SuppressLint("CheckResult")
    private fun deleteFormDetails(vararg formDetails: FormDetails) {
        db.formDetailsDao().deleteForms(*formDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
                Log.i(TAG, it.message.orEmpty())
            })
    }

    @SuppressLint("CheckResult")
    private fun saveFormDetails(list: List<FormDetails>) {
        db.formDetailsDao().saveForm(*list.map { it }.toTypedArray()).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                list.forEach { getFormQuestions(it) }
            }
    }

    @SuppressLint("CheckResult")
    private fun saveFormDetails(formDetails: FormDetails) {
        db.formDetailsDao().saveForm(formDetails).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                getFormQuestions(formDetails)
            }
    }

    private fun processFormDetailsData(
        dbFormDetails: List<FormDetails>?,
        response: List<FormDetails>?
    ) {
        if (response == null) {
            return
        }
        val apiFormDetails = response
        if (dbFormDetails == null || dbFormDetails.isEmpty()) {
            saveFormDetails(apiFormDetails)
            return
        }
        if (apiFormDetails.size < dbFormDetails.size) {
            dbFormDetails.minus(apiFormDetails).also { diff ->
                if (diff.isNotEmpty()) {
                    deleteFormDetails(*diff.map { it }.toTypedArray())
                }
            }
        }
        apiFormDetails.forEach { apiForm ->
            val dbForm = dbFormDetails.find { it.id == apiForm.id }
            if (dbForm != null && apiForm.formVersion != dbForm.formVersion) {
                deleteFormDetails(dbForm)
                saveFormDetails(apiForm)
            }
            if (dbForm == null) {
                saveFormDetails(apiForm)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun getFormQuestions(form: FormDetails) {
        apiInterface.getForm(form.id).doOnNext { list ->
            list.forEach { section ->
                section.formId = form.id
                section.questions.forEach { question ->
                    question.sectionId = section.sectionId
                    question.optionsToQuestions.forEach { answer ->
                        answer.questionId = question.questionId
                    }
                }
            }
            db.formDetailsDao().save(*list.map { it }.toTypedArray())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
                Log.i(TAG, it.message.orEmpty())
            })
    }

    open fun getAnswersForForm(
        formId: Int,
        beneficiaryId: Int
    ): Observable<List<AnsweredQuestionPOJO>> {
        return db.formDetailsDao().getAnswersForForm(formId, beneficiaryId)
    }

    @SuppressLint("CheckResult")
    open fun saveAnsweredQuestion(
        answeredQuestion: AnsweredQuestion,
        answers: List<SelectedAnswer>
    ): Single<Int> {
        return Single.create<Int> {
            db.formDetailsDao().insertAnsweredQuestion(answeredQuestion, answers)
            it.onSuccess(1)
        }.doOnError {
            Log.e("SAVING_ERROR", "HELP")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    @SuppressLint("CheckResult")
    fun getAnsweredQuestionsForForm(
        beneficiaryId: Int,
        formId: Int
    ): Observable<List<AnsweredQuestion>> {
        return db.formDetailsDao().getAnsweredQuestionsForForm(beneficiaryId, formId)
            .subscribeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    fun getAnsweredQuestion(
        questionId: Int,
        beneficiaryId: Int,
        formId: Int
    ): Observable<List<AnsweredQuestion>> {
        return db.formDetailsDao().getAnsweredQuestions(questionId, beneficiaryId, formId)
            .subscribeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    open fun deleteAnsweredQuestion(answeredQuestion: AnsweredQuestion): Single<Int> {
        return db.formDetailsDao().deleteAnsweredQuestion(answeredQuestion.id)
            .flatMap {
                Log.d("COMPLETABLE", "Before delete selected Answers")
                db.formDetailsDao().deleteSelectedAnswers(answeredQuestion.id)
            }
            .subscribeOn(Schedulers.io())
    }

    private fun syncAnswers(
        formId: Int,
        completionDate: Date?,
        list: List<AnsweredQuestionPOJO>
    ): Single<Int> {
        val responseAnswerContainer = ResponseAnswerContainer()
        responseAnswerContainer.completionDate = completionDate
        responseAnswerContainer.formId = formId

        if(list.isNotEmpty()) {
            responseAnswerContainer.answers = list.map {
                it.answeredQuestion.options = it.selectedAnswers
                return@map it.answeredQuestion
            }
        }
        return apiInterface.postQuestionAnswer(responseAnswerContainer)
            .map {
            0
        }.doOnError {
                Log.e("ERROR_POST_ANS", "${it.localizedMessage.orEmpty()}")
            }
            .doFinally {
                Log.e("ERROR_POST_ANS", " REQUEST ENDED")
            }
    }

    open fun getNotSyncedQuestions(formId: Int?): Single<Int> =
        db.formDetailsDao().getCountOfNotSyncedQuestions()

    open fun getNotSyncedQuestionsObservable(formId: Int?): Observable<Int> =
        db.formDetailsDao().getCountOfNotSyncedQuestionsObservable()

    open fun getNoteDetails(noteId: Int): Single<Note?> {
        return db.noteDao().getNote(noteId)
    }

    fun getRemoteNotes(beneficiaryId: Int, formId: Int? = null): Single<List<Note>> {
        return apiInterface.getNotes(beneficiaryId, formId)
            .flatMap { notes ->
                val removed: Int
                if(formId == null) {
                    removed = db.noteDao().removeSyncedNotes(beneficiaryId)
                } else {
                    removed = db.noteDao().removeSyncedNotes(beneficiaryId, formId)
                }

                notes.map {
                    it.beneficiaryId = beneficiaryId
                    it.formId = formId
                    it.synced = true
                }
                Log.d("SYNC_NOTE", "removed $removed")
                db.noteDao().save(notes).map { notes }
            }.doOnError {
                Log.e("SYNC_NOTE", "${it.localizedMessage.orEmpty()}")
            }

    }

    open fun getQuestionNotes(
        beneficiaryId: Int,
        formId: Int,
        selectedQuestionId: Int
    ): Observable<List<Note>> {
        return db.noteDao().getNotesForQuestion(beneficiaryId, formId, selectedQuestionId)
    }

    fun getQuestionNotes(
        beneficiaryId: Int,
        formId: Int
    ): Observable<List<Note>> {
        return db.noteDao().getNotesForQuestion(beneficiaryId, formId)
    }

    open fun getBeneficiaryGenericNotes(beneficiaryId: Int): Single<List<Note>> {
        return db.noteDao().getBeneficiaryGenericNotes(beneficiaryId)
    }

    open fun getNotSyncedNotes(): Observable<Int> = db.noteDao().getCountOfNotSyncedNotes()

    open fun saveNote(note: Note): Single<ResponseBody> =
        db.noteDao().save(listOf(note)).flatMap {
            note.id = it[0].toInt()
            postNote(note)
        }

    private fun postNote(note: Note): Single<ResponseBody> {
        var body: MultipartBody.Part? = null
        var questionId = 0
        var beneficiaryId = 0
        note.attachmentPath?.let {
            val file = File(it)
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("File", file.name, requestFile)
        }
        note.questionId?.let {
            questionId = it
        }
        note.beneficiaryId?.let {
            beneficiaryId = it
        }

        return apiInterface.postNote(
            body,
            beneficiaryId.toString().createMultipart("BeneficiaryId"),
            questionId.toString().createMultipart("QuestionId"),
            note.text?.createMultipart("Text")
        ).doOnSuccess {
            note.synced = true
        }.doFinally {
            db.noteDao().updateNote(note)
        }
    }

    @SuppressLint("CheckResult")
    open fun syncData(selectedFormInfo: SelectedFormInfo): Observable<List<FilledInAnswersResponse>> {

        return Single.merge(
            mutableListOf(
                syncAnswersObservable(selectedFormInfo),
                syncNotesObservable()
            )
        )
            .flatMapSingle {
                apiInterface.getRemoteAnswers(
                    selectedFormInfo.beneficiaryId,
                    selectedFormInfo.formId
                ).subscribeOn(Schedulers.io())
            }
            .doAfterTerminate {
                syncInProgress = false
            }.toObservable()
    }

    @SuppressLint("CheckResult")
    open fun syncAnswersObservable(selectedFormInfo: SelectedFormInfo): Single<Int> {
        lateinit var answers: List<AnsweredQuestionPOJO>
        return db.formDetailsDao().getNotSyncedQuestions(false, selectedFormInfo.formId)
            .flatMap {
                answers = it
                syncAnswers(selectedFormInfo.formId, selectedFormInfo.completionDate, it)
            }
            .flatMap { body ->
                answers.forEach { item -> item.answeredQuestion.synced = true }
                Single.create<Int> { emitter ->
                    val result = db.formDetailsDao()
                        .updateAnsweredQuestion(*answers.map { it.answeredQuestion }.toTypedArray())
                    emitter.onSuccess(result)
                }
            }
            .subscribeOn(Schedulers.io())

    }

    open fun saveToken(accessToken: String) {
        sharedPreferences.saveToken(accessToken)
    }

    open fun getToken() = sharedPreferences.getToken()
    open fun deleteToken() = sharedPreferences.deleteToken()

    open fun isAuthenticationVerified() = sharedPreferences.isAuthenticationVerified()
    open fun verifiedAuthentication() = sharedPreferences.verifiedAuthentication()
    open fun resetAuthenticationVerification() = sharedPreferences.resetAuthenticationVerification()

    open fun changedPassword() { sharedPreferences.changedPassword() }
    open fun hasChangedPassword() = sharedPreferences.hasChangedPassword()
    open fun resetHasChangedPassword() = sharedPreferences.resetHasChangedPassword()

    open fun hasCompletedOnboarding() = sharedPreferences.hasCompletedOnboarding()
    open fun completedOnboarding() = sharedPreferences.completedOnboarding()
    open fun resetHasCompletedOnboarding() = sharedPreferences.resetHasCompletedOnboarding()

    fun verifyCode(code:String): Single<CodeVerificationResponse> {
        return apiInterface.verifyCode(CodeVerification(code))
    }

    fun resendCode(): Single<ResendCodeVerificationResponse> {
        return apiInterface.resendCode()
    }

    @SuppressLint("CheckResult")
    private fun syncNotesObservable(): Single<Int> {

        return Single.fromObservable(db.noteDao().getNotSyncedNotes().take(1))
            .flatMap {
                Single.merge(it.map {note ->
                    postNote(note)
                }).reduce(0,
                    { acc, newElement ->
                        acc + 0
                        acc }  )
            }.onErrorReturn { 0 }

    }

    @SuppressLint("CheckResult")
    fun getCounties(): Observable<List<County>> {
        return db.countiesDao().getAll()
    }

    fun getCountyById(id: Int): Single<County?> {
        return db.countiesDao().getCountyById(id).subscribeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    fun getCitiesByCounty(countyId: Int): Observable<List<City>> {
        return db.citiesDao().getCountyCities(countyId)
    }

    fun getCityById(id: Int): Single<City?> {
        return db.citiesDao().getCityById(id).subscribeOn(Schedulers.io())
    }

    fun saveCounties(counties: List<County>) {
        return db.countiesDao().save(counties)
    }

    fun saveCities(cities: List<City>) {
        return db.citiesDao().save(cities)
    }

    fun getPatientForm(formId: Int, beneficiaryId: Int): Single<PatientForm> {
        return db.formDetailsDao()
            .getPatientForm(formId, beneficiaryId)
            .subscribeOn(Schedulers.io())
    }
}
