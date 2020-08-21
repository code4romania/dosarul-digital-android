package ro.code4.casefile.ui.patient.addpatient

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class AddUpdatePatientModel  (
    @Expose
    var name: String? = null,
    @Expose
    var beneficiaryId: Int? = null,
    @Expose
    var birthDate: Date? = null,
    @Expose
    var civilStatus: Int? = null,
    @Expose
    var countyId: Int? = null,
    @Expose
    var cityId: Int? = null,
    @Expose
    var gender: Int? = null,
    @Expose
    var isFamilyOfBeneficiaryId: Int? = null,
    @Expose
    var newAllocatedFormsIds: List<Int>? = null,
    @Expose
    var dealocatedFormsIds: List<Int>? = null,
    @Expose
    var formsIds: List<Int>? = null
): Parcelable

