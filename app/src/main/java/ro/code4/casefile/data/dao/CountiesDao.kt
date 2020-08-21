package ro.code4.casefile.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ro.code4.casefile.data.model.City
import ro.code4.casefile.data.model.County

@Dao
interface CountiesDao {
    @Query("SELECT * FROM counties")
    fun getAll(): Observable<List<County>>

    @Query("SELECT * FROM counties where countyId=:id limit 1")
    fun getCountyById(id: Int): Single<County?>

    @Insert(onConflict = REPLACE)
    fun save(counties: List<County>)

}
