package ro.code4.casefile.ui.patient.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ro.code4.casefile.ui.patient.addpatient.UiMaritalStatus

@Parcelize
class PatientUiModel (var id: Int = -1,
                      var name: String? = null,
                      var age: Int? = null,
                      var maritalStatus: UiMaritalStatus = UiMaritalStatus.UNDEFINED,
                      var city: String? = null,
                      var county: String? = null) : Parcelable {

}
