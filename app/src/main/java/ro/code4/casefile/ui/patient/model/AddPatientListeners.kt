package ro.code4.casefile.ui.patient.model

import ro.code4.casefile.ui.patient.addpatient.SavePatientListener
import ro.code4.casefile.widget.spinner.ClickListener

data class AddPatientListeners(
    val dateOfBirthListener: ClickListener,
    val maritalStatusListener: ClickListener,
    val countyListener: ClickListener,
    val cityListener: ClickListener,
    val genderListener: ClickListener,
    val savePatientListener: SavePatientListener
)
