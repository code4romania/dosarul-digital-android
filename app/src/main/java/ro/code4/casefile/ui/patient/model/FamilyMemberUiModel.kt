package ro.code4.casefile.ui.patient.model

import android.view.View

data class FamilyMemberUiModel(
    val id: Int,
    val name: String,
    val relation: String = "",
    val relationVisibility: Int = View.GONE
)
