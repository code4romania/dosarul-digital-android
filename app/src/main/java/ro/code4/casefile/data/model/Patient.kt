package ro.code4.casefile.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import ro.code4.casefile.ui.patient.addpatient.MaritalStatus

@Entity(tableName = "patients")

@Parcelize
data class Patient(
    @PrimaryKey
    @Expose
    val beneficiaryId: Int = 0,
    @Expose
    val name: String?,
    @Expose
    val age: Int = 0,
    @Expose
    val civilStatus: MaritalStatus?,
    @Expose
    val county: String?,
    @Expose
    val city: String?
) : Parcelable

