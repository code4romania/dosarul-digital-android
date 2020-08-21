package ro.code4.casefile.ui.patient.model

import android.view.View

data class PatientFamilyMembersUiModel(
    val familyMembers: List<FamilyMemberUiModel>,
    val familyMembersListVisibility: Int = View.GONE,
    val noFamilyMembersLayoutVisibility: Int = View.VISIBLE
)
