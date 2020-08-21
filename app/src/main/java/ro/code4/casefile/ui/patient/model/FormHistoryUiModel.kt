package ro.code4.casefile.ui.patient.model

import androidx.annotation.DrawableRes

data class FormHistoryUiModel(
    val formId: Int,
    val formDate: String,
    val formTitle: String,
    @DrawableRes val syncIconRes: Int
)
