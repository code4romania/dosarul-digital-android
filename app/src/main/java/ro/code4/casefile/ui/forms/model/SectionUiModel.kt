package ro.code4.casefile.ui.forms.model

import android.view.View

data class SectionUiModel(
    val title: String?,
    val description: String?,
    val descriptionVisibility: Int = View.GONE
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SectionUiModel

        if (title != other.title) return false
        if (description != other.description) return false
        if (descriptionVisibility != other.descriptionVisibility) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + descriptionVisibility
        return result
    }
}
