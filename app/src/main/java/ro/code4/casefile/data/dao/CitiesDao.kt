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
interface CitiesDao {
    @Query("SELECT * FROM cities where countyId=:countyId ")
    fun getCountyCities(countyId: Int): Observable<List<City>>

    @Query("SELECT * FROM cities where cityId=:id limit 1")
    fun getCityById(id: Int): Single<City?>

    @Insert(onConflict = REPLACE)
    fun save(city: List<City>)

}
