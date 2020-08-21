package ro.code4.casefile.ui.navigation

import ro.code4.casefile.ui.forms.model.SelectedFormInfo
import ro.code4.casefile.ui.patient.addpatient.AddUpdatePatientModel

sealed class NavigationEvent

class NavigationQuestionNote(val questionId: Int?, val formId: Int?, val beneficiaryId: Int?) : NavigationEvent()
class NavigationAddBeneficiaryNote(val beneficiaryId: Int?) : NavigationEvent()
class NavigationEditBeneficiaryNote(val beneficiaryId: Int?, val noteId: Int?) : NavigationEvent()
class NavigationQuestionnaires : NavigationEvent()
class NavigationFormQuestions(val selectedFormInfo: SelectedFormInfo) : NavigationEvent()
class NavigationPatientFormSelection(val patient: AddUpdatePatientModel) : NavigationEvent()
class NavigationFormFillDate(val selectedFormInfo: SelectedFormInfo) : NavigationEvent()
class NavigationQuestionDetails(val payload: SelectedFormInfo, val questionId: Int) : NavigationEvent()
class NavigationPatientDetails(val patientId: Int) : NavigationEvent()
class NavigationPatientDetailsForm(val patientId: Int) : NavigationEvent()
class NavigationAddPatient : NavigationEvent()
class NavigationEditPatient(val patientId: Int) : NavigationEvent()
class NavigationPatientsList : NavigationEvent()
class NavigationAddFamilyMember(val patientId: Int) : NavigationEvent()

//Login events
class NavigationLogin : NavigationEvent()
class NavigationCodeVerification : NavigationEvent()
class NavigationChangePassword : NavigationEvent()
class NavigationWelcome : NavigationEvent()
class NavigationOnboarding : NavigationEvent()
