package ro.code4.casefile.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ro.code4.casefile.data.model.Patient
import ro.code4.casefile.data.model.PatientDetails

@Dao
interface PatientDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(patientDetails: List<PatientDetails>): Single<List<Long>>

    @Update
    fun update(patientDetails: PatientDetails): Completable

    @Query("SELECT * FROM patient_details WHERE beneficiaryId=:patientId")
    fun getPatientDetails(patientId: Int): Maybe<PatientDetails>

    @Query("SELECT * FROM patient_details")
    fun getPatientDetails(): Maybe<List<PatientDetails>>



    @Delete
    fun delete(patientDetails: PatientDetails): Completable
}
