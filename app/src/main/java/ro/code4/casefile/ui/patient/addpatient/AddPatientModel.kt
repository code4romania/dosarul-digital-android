
package ro.code4.casefile.ui.patient.addpatient

import ro.code4.casefile.data.model.City
import ro.code4.casefile.data.model.County
import java.util.*

class AddPatientModel {

    var name: String? = null
    var beneficiaryId: Int? = null
    var dateOfBirth: Date? = null
    var maritalStatus: UiMaritalStatus? = null
    var county: County? = null
    var city: City? = null
    var gender: Gender? = null
//    var familyRelation: FamilyRelation? = null
}

fun AddPatientModel.toEntity(familyBeneficiaryId: Int?) =
    AddUpdatePatientModel(
        beneficiaryId = beneficiaryId,
        name = name,
        birthDate = dateOfBirth,
        civilStatus = maritalStatus?.toMaritalStatus()?.id,
        countyId = county?.countyId,
        cityId = city?.cityId,
        gender = gender?.id,
        isFamilyOfBeneficiaryId = familyBeneficiaryId
    )
