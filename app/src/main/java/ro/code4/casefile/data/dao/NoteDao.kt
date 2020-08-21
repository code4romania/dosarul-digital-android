package ro.code4.casefile.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ro.code4.casefile.data.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(notes: List<Note>): Single<List<Long>>

    @Update
    fun updateNote(vararg note: Note)

    @Query("SELECT * FROM note WHERE beneficiaryId=:beneficiaryId AND formId=:formId AND questionId=:questionId ORDER BY lastModified DESC")
    fun getNotesForQuestion(beneficiaryId: Int, formId: Int? = null,  questionId: Int? = null): Observable<List<Note>>

    @Query("SELECT * FROM note WHERE beneficiaryId=:beneficiaryId AND formId=:formId  ORDER BY lastModified DESC")
    fun getNotesForQuestion(beneficiaryId: Int, formId: Int? = null): Observable<List<Note>>

    @Query("SELECT * FROM note WHERE id=:noteId")
    fun getNote(noteId: Int): Single<Note?>

    @Query("SELECT * FROM note WHERE beneficiaryId=:beneficiaryId ORDER BY lastModified DESC")
    fun getNotes(beneficiaryId: Int): Single<List<Note>>

    @Query("SELECT * FROM note WHERE beneficiaryId=:beneficiaryId AND (questionId is null OR questionId=0) ORDER BY lastModified DESC")
    fun getBeneficiaryGenericNotes(beneficiaryId: Int): Single<List<Note>>

    @Query("SELECT * FROM note WHERE synced=:synced")
    fun getNotSyncedNotes(synced: Boolean = false): Observable<List<Note>>

    @Query("SELECT COUNT(*) FROM note WHERE synced =:synced")
    fun getCountOfNotSyncedNotes(synced: Boolean = false): Observable<Int>

    @Query("DELETE FROM note WHERE beneficiaryId=:beneficiaryId AND formId=:formId AND synced=1")
    fun removeSyncedNotes(beneficiaryId: Int, formId: Int): Int

    @Query("DELETE FROM note WHERE beneficiaryId=:beneficiaryId AND formId is NULL AND synced=1")
    fun removeSyncedNotes(beneficiaryId: Int): Int

}
