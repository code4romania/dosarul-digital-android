package ro.code4.casefile.ui.patient

data class PatientDetailsListeners(
    val editPatient: () -> Unit,
    val patientForms: () -> Unit,
    val sendRecord: () -> Unit,
    val addFamilyMember: () -> Unit,
    val addNote: () -> Unit
)
