package ro.code4.casefile.ui.forms.model

data class FormSelectionItemUiModel(
    val id: Int,
    val title: String,
    var isSelected: Boolean = false
)
