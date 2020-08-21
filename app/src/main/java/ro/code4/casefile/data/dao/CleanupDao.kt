
package ro.code4.casefile.data.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ro.code4.casefile.data.model.answers.AnsweredQuestion

@Dao
interface CleanupDao {

    @Query("DELETE FROM family_members WHERE isFamilyOfBeneficiaryId=:beneficiaryId")
    fun removePatientFamilyMembers(beneficiaryId: Int): Int

    @Query("DELETE FROM patient_forms WHERE beneficiaryId=:beneficiaryId")
    fun removePatientForms(beneficiaryId: Int): Int

    @Query("SELECT * FROM answered_question WHERE beneficiaryId=:beneficiaryId")
    fun getAnswersForBeneficiary(
        beneficiaryId: Int
    ): List<AnsweredQuestion>

    @Query("DELETE FROM selected_answer WHERE answeredQuestionId=:answeredQuestionId")
    fun removePatientAnswers(answeredQuestionId: Int): Int

    @Query("DELETE FROM note WHERE beneficiaryId=:beneficiaryId")
    fun removePatientNotes(beneficiaryId: Int): Int

    @Query("DELETE FROM answered_question WHERE beneficiaryId=:beneficiaryId")
    fun removePatientAnsweredQuestions(beneficiaryId: Int): Int

    @Query("DELETE FROM patient_details where beneficiaryId=:beneficiaryId")
    fun removePatientDetails(beneficiaryId: Int): Int

    @Query("DELETE FROM patients where beneficiaryId=:beneficiaryId")
    fun removePatient(beneficiaryId: Int): Int

    @Transaction
    fun removeBeneficiary(beneficiaryId: Int) {
        Log.d("CLEANUP", "Removing beneficiary with ID: $beneficiaryId")
        removePatientFamilyMembers(beneficiaryId).toConsole("family members")
        removePatientForms(beneficiaryId).toConsole("patient forms")
        getAnswersForBeneficiary(beneficiaryId).map {
           removePatientAnswers(it.id).toConsole("patient answers")
        }
        removePatientNotes(beneficiaryId).toConsole("patient notes")
        removePatientAnsweredQuestions(beneficiaryId).toConsole("answered question")
        removePatientDetails(beneficiaryId).toConsole("patient details")
        removePatient(beneficiaryId).toConsole("patients")
    }
}

fun Int.toConsole(from: String) {
    Log.d("CLEANUP", "Removed $this from $from")
}
