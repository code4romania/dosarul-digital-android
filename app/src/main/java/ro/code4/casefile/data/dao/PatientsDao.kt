package ro.code4.casefile.data.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ro.code4.casefile.data.model.Patient
import ro.code4.casefile.data.model.PatientDetailsFamilyMember

@Dao
interface PatientsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(patients: List<Patient>): Single<List<Long>>

    @Update
    fun update(patient: Patient): Completable

    @Query("SELECT * FROM patients WHERE beneficiaryId=:patientId")
    fun getPatient(patientId: Int): Maybe<Patient>

    @Query("SELECT * FROM family_members WHERE isFamilyOfBeneficiaryId=:patientId")
    fun getPatientFamilyMembers(patientId: Int): Observable<List<PatientDetailsFamilyMember>>

    @Query("SELECT * FROM patients")
    fun getPatients(): Observable<List<Patient>>

    @Delete
    fun delete(patient: Patient): Completable
}
