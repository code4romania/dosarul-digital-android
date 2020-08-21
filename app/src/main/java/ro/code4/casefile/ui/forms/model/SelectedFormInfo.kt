package ro.code4.casefile.ui.forms.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class SelectedFormInfo(
    val formId: Int,
    val title: String,
    val beneficiaryId: Int,
    var completionDate: Date?
) : Parcelable
