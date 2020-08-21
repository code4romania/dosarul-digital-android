package ro.code4.casefile.repositories

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import retrofit2.Retrofit
import ro.code4.casefile.data.AppDatabase
import ro.code4.casefile.data.model.FormDetails
import ro.code4.casefile.data.model.Patient
import ro.code4.casefile.helper.*
import ro.code4.casefile.services.ApiInterface

class MockRepository : Repository() {

    companion object {
        @JvmStatic
        val TAG = Repository::class.java.simpleName
    }

    private val retrofit: Retrofit by inject()
    private val db: AppDatabase by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val apiInterface: ApiInterface by inject()

    private var syncInProgress = false

    override fun getPatients(): Observable<List<Patient>> {
        return db.patientsDao().getPatients()
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

    override fun saveToken(accessToken: String) {
        sharedPreferences.saveToken(accessToken)
    }

    override fun getToken() = sharedPreferences.getToken()

    override fun changedPassword() {
        sharedPreferences.changedPassword()
    }

    override fun hasChangedPassword() = sharedPreferences.hasChangedPassword()

    override fun hasCompletedOnboarding() = sharedPreferences.hasCompletedOnboarding()
}
