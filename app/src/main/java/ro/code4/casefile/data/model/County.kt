package ro.code4.casefile.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.parceler.Parcel

@Entity(tableName = "counties")
@Parcel(Parcel.Serialization.FIELD)
@Parcelize
class County: Parcelable {

    @PrimaryKey
    @Expose
    var countyId: Int = 0

    @Expose
    lateinit var code: String

    @Expose
    lateinit var name: String

    @Expose
    var order: Int = 0

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (other !is County) {
            return false
        }
        return code == other.code
    }

    override fun hashCode(): Int {
        var result = countyId
        result = 31 * result + code.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + order
        return result
    }


}
