package ro.code4.casefile.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import org.parceler.Parcel

@Entity(tableName = "cities",
    foreignKeys = [ForeignKey(
    entity = County::class,
    parentColumns = ["countyId"],
    childColumns = ["countyId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)])
@Parcel(Parcel.Serialization.FIELD)
@Parcelize
class City: Parcelable {

    @PrimaryKey
    @Expose
    var cityId: Int = 0

    @Expose
    lateinit var name: String

    @Expose
    var countyId: Int = -1

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (other !is City) {
            return false
        }
        return cityId == other.cityId
    }

    override fun hashCode(): Int {
        var result = cityId
        result = 31 * result + name.hashCode()
        return result
    }
}
