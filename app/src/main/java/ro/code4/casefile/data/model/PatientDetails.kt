
package ro.code4.casefile.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.util.*

@Entity(tableName = "patient_details")

class PatientDetails (){
    @PrimaryKey
    @Expose
    var beneficiaryId: Int = 0
    @Expose
    var userId: Int? = 0
    @Expose
    var name: String? = null
    @Expose
    var birthDate: Date? = null
    @Expose
    var civilStatus: Int? = null
    @Expose
    var countyId: Int? = null
    @Expose
    var cityId: Int? = null
    @Expose
    var gender: Int? = null
    @Expose
    @Ignore
    var familyMembers: List<PatientDetailsFamilyMember>? = null
    @Expose
    @Ignore
    var forms: List<PatientForm>? = null

    constructor(beneficiaryId: Int = 0,
    userId: Int?,
    name: String?,
    birthDate: Date?,
    civilStatus: Int?,
    countyId: Int?,
    cityId: Int?,
    gender: Int?,
    familyMembers: List<PatientDetailsFamilyMember>? = null,
    forms: List<PatientForm>? = null): this() {
        this.beneficiaryId = beneficiaryId
        this.userId = userId
        this.name = name
        this.birthDate = birthDate
        this.civilStatus = civilStatus
        this.countyId = countyId
        this.cityId = cityId
        this.gender = gender
        this.familyMembers = familyMembers
        this.forms = forms
    }
}



