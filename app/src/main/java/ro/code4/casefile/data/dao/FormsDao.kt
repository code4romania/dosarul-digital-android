package ro.code4.casefile.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ro.code4.casefile.data.model.*
import ro.code4.casefile.data.model.answers.AnsweredQuestion
import ro.code4.casefile.data.model.answers.SelectedAnswer
import ro.code4.casefile.data.pojo.AnsweredQuestionPOJO
import ro.code4.casefile.data.pojo.FormWithSections
import ro.code4.casefile.data.pojo.SectionWithQuestions

@Dao
interface FormsDao {
    @Query("SELECT * FROM form_details")
    fun getAllForms(): Observable<List<FormDetails>>

    @Insert(onConflict = REPLACE)
    fun saveForm(vararg forms: FormDetails): Completable

    @Insert(onConflict = REPLACE)
    fun savePatientForm(forms: List<PatientForm>): Single<List<Long>>

    @Insert(onConflict = REPLACE)
    fun savePatientForm(form:PatientForm): Single<Long>

    @Insert(onConflict = REPLACE)
    fun savePatientFamilyMembers(forms: List<PatientDetailsFamilyMember>): Single<List<Long>>

    @Delete
    fun deleteForms(vararg forms: FormDetails): Completable

    @Query("SELECT * FROM section WHERE code=:formId")
    fun getSectionsByCode(formId: Int): Maybe<List<Section>>

    @Transaction
    fun save(vararg sections: Section) {
        val saved = saveSections(*sections)
        val questions = sections.fold(ArrayList<Question>(), { list, section ->
            list.addAll(section.questions)
            list
        })
        saveQuestions(*questions.map { it }.toTypedArray())
        val answers = questions.fold(ArrayList<Answer>(), { list, question ->
            list.addAll(question.optionsToQuestions)
            list
        })
        saveAnswers(*answers.map { it }.toTypedArray())
    }

    @Insert(onConflict = REPLACE)
    fun saveSections(vararg sections: Section): List<Long>

    @Insert(onConflict = REPLACE)
    fun saveQuestions(vararg questions: Question)

    @Insert(onConflict = REPLACE)
    fun saveAnswers(vararg answers: Answer)

    @Query("DELETE FROM answered_question WHERE id=:id")
    fun deleteAnsweredQuestion(id: Int): Single<Int>


    @Query("DELETE FROM selected_answer WHERE answeredQuestionId=:answeredQuestionId")
    fun deleteSelectedAnswers(answeredQuestionId: Int): Single<Int>

    @Query("SELECT * FROM answered_question WHERE beneficiaryId=:beneficiaryId")
    fun getAnswersFor(
        beneficiaryId: Int
    ): Observable<List<AnsweredQuestionPOJO>>


    @Query("SELECT * FROM form_details")
    fun getFormsWithSections(): Observable<List<FormWithSections>>

    @Query("SELECT * FROM section where formId=:formId")
    fun getSectionsWithQuestions(formId: Int): Observable<List<SectionWithQuestions>>

    @Query("SELECT * FROM section")
    fun getSections(): Observable<List<Section>>

    @Query("SELECT * FROM answered_question WHERE formId=:formId and beneficiaryId=:beneficiaryId")
    fun getAnswersForForm(
        formId: Int,
        beneficiaryId: Int
    ): Observable<List<AnsweredQuestionPOJO>>

    @Query("SELECT * FROM answered_question WHERE formId=:formId AND synced=:synced")
    //TODO maybe beneficiary too ?
    fun getNotSyncedQuestionsForForm(
        formId: Int,
        synced: Boolean = false
    ): Maybe<List<AnsweredQuestionPOJO>>

    @Query("SELECT * FROM answered_question WHERE formId=:formId AND synced=:synced")
    fun getNotSyncedQuestions(synced: Boolean = false, formId: Int): Single<List<AnsweredQuestionPOJO>>

    @Transaction
    fun insertAnsweredQuestion(answeredQuestion: AnsweredQuestion, answers: List<SelectedAnswer>) {
        val id = insertAnsweredQuestion(answeredQuestion)
        val answerIds = insertAnswers(*answers.map {
            it.answeredQuestionId = id.toInt()
            it
        }.toTypedArray())
        answeredQuestion.id = id.toInt()
        answeredQuestion.savedLocally = true
        updateAnsweredQuestion(answeredQuestion)
    }

    @Insert(onConflict = REPLACE)
    fun insertAnsweredQuestion(answeredQuestion: AnsweredQuestion): Long

    @Update
    fun updateAnsweredQuestion(vararg answeredQuestion: AnsweredQuestion): Int

    @Insert(onConflict = REPLACE)
    fun insertAnswers(vararg answers: SelectedAnswer): List<Long>

    @Query("UPDATE answered_question SET synced=:synced WHERE formId=:formId")
    fun updateAnsweredQuestions(
        formId: Int,
        synced: Boolean = true
    )

    @Query("SELECT * FROM answered_question  WHERE questionId=:questionId AND beneficiaryId=:beneficiaryId AND formId=:formId")
    fun getAnsweredQuestions(
        questionId: Int,
        beneficiaryId: Int,
        formId: Int
    ): Observable<List<AnsweredQuestion>>

    @Query("SELECT * FROM answered_question  WHERE beneficiaryId=:beneficiaryId AND formId=:formId")
    fun getAnsweredQuestionsForForm(
        beneficiaryId: Int,
        formId: Int
    ): Observable<List<AnsweredQuestion>>

    @Query("SELECT COUNT(*) FROM answered_question WHERE  synced=:synced")
    fun getCountOfNotSyncedQuestions(synced: Boolean = false): Single<Int>

    @Query("SELECT COUNT(*) FROM answered_question WHERE  synced=:synced")
    fun getCountOfNotSyncedQuestionsObservable(synced: Boolean = false): Observable<Int>

    @Query("SELECT COUNT(*) FROM answered_question WHERE  synced=:synced AND formId=:formId")
    fun getCountOfNotSyncedQuestionsByForm(formId: Int, synced: Boolean = false): LiveData<Int>

    @Query("SELECT * FROM patient_forms where beneficiaryId=:beneficiaryId")
    fun getPatientForms(beneficiaryId: Int): Observable<List<PatientForm>>

    @Query("SELECT * FROM patient_forms where formId=:formId and beneficiaryId=:beneficiaryId limit 1")
    fun getPatientForm(formId: Int, beneficiaryId: Int): Single<PatientForm>

    @Query("SELECT * FROM patient_forms where beneficiaryId=:beneficiaryId")//TODO preferably get only completed forms noQuestions==noAnsweredQuestions
    fun getPatientCompletedForms(beneficiaryId: Int): Observable<List<PatientForm>>

    @Query("SELECT * FROM patient_forms")
    fun getAllPatientForms(): Observable<List<PatientForm>>


}
