
package ro.code4.casefile.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "family_members")
data class PatientDetailsFamilyMember(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @Expose
    var beneficiaryId: Int,
    @Expose
    var name: String,
    @Expose
    var isFamilyOfBeneficiaryId: Int
) : Parcelable
