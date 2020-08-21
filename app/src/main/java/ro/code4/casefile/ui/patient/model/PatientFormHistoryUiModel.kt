package ro.code4.casefile.ui.patient.model

import android.view.View

data class PatientFormHistoryUiModel(
    val formHistoryList: List<FormHistoryUiModel>,
    val formHistoryListVisibility: Int = View.GONE,
    val noFormHistoryLayoutVisibility: Int = View.VISIBLE
)
