
package ro.code4.casefile.sync

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import ro.code4.casefile.data.model.FormDetails
import ro.code4.casefile.data.model.Patient
import ro.code4.casefile.data.model.PatientForm
import ro.code4.casefile.data.model.answers.AnsweredQuestion
import ro.code4.casefile.data.model.answers.SelectedAnswer
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.services.ApiInterface

class PatientsWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams), KoinComponent {

    private val repository: Repository by inject()

    private fun processPatientsWithForms() = BiFunction<List<FormDetails>?, List<Patient>, List<FormWithBeneficiary>> { forms, patients ->
        val result = mutableListOf<FormWithBeneficiary>()
        forms.map {form ->
            patients.map { patient ->
                result.add(FormWithBeneficiary(patient.beneficiaryId, form.id))
            }
        }
        result
    }

    override fun doWork(): Result {

        val forms = Single.fromObservable(repository.getForms().take(1))
        val patients = repository.getPatientsFromServer()
            .flatMap { remotePatients ->
                Single.fromObservable(repository.getPatients().take(1))
                    .flatMap { localPatients ->
                        val diff = localPatients.filterNot { remotePatients.contains(it) }

                        diff.map { removablePatient ->
                            repository.removeBeneficiary(removablePatient.beneficiaryId)
                        }

                        Single.merge(remotePatients.map { patient ->
                            repository.getPatientDetailsFromServer(patient.beneficiaryId)
                                .flatMap {
                                    repository.getRemoteNotes(patient.beneficiaryId)
                                }.map {
                                    patient
                                }
                        }).reduce(mutableListOf<Patient>(),
                            { acc, newElement ->
                                acc.add(newElement)
                                acc
                            })
                    }
            }


        var result = Result.success()
        Single.zip(
            forms,
            patients,
            processPatientsWithForms()
        ).flatMapObservable {
            val calls = mutableListOf<Observable<*>>()
            it.map {pair ->
                calls.add(
                    repository.getRemoteAnswers(pair.beneficiaryId, pair.formId)
                        .subscribeOn(Schedulers.io()).flatMapObservable {filledAnswers ->
                            Single.merge(
                                filledAnswers.map { filledInAnswer ->
                                    val ansq = AnsweredQuestion(
                                        filledInAnswer.questionId,
                                        pair.beneficiaryId,
                                        filledInAnswer.formId
                                    )
                                    ansq.savedLocally = true
                                    ansq.synced = true

                                    val options = mutableListOf<SelectedAnswer>()
                                    filledInAnswer.answers?.map { answer ->

                                        options.add(
                                            SelectedAnswer(
                                                answer.idOption,
                                                answer.value
                                            )
                                        )
                                    }
                                    repository.saveAnsweredQuestion(ansq, options)
                                }
                            ).toObservable().take(1)
                        })
            }
            it.map { formWithBeneficiary ->
                calls.add(repository.getRemoteNotes(formWithBeneficiary.beneficiaryId, formWithBeneficiary.formId).toObservable().take(1))
            }
            Observable.merge(
                calls
            )
        }.blockingSubscribe({
            Log.d("PATIENT_DETAILS", "SAVED PATIENT")
        }, {
            result = Result.retry()
        })

        // Indicate whether the work finished successfully with the Result
        return result
    }
}

data class FormWithBeneficiary(val beneficiaryId: Int, val formId: Int)
