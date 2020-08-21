package ro.code4.casefile.ui.patient.model

import ro.code4.casefile.widget.spinner.SpinnerUiModel

data class AddPatientUiModel(
    var dateOfBirthUiModel: SpinnerUiModel,
    var maritalStatusUiModel: SpinnerUiModel,
    var countyUiModel: SpinnerUiModel,
    var cityUiModel: SpinnerUiModel,
    var genderUiModel: SpinnerUiModel
//    var familyRelationUiModel: SpinnerUiModel
)
