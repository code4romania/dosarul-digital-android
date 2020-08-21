package ro.code4.casefile.ui.forms.model

data class AnswerUiModel(
    val idOption: Int,
    var text: String,
    val isFreeText: Boolean,
    var value: String?,
    var isSelected: Boolean
) {
    override fun equals(other: Any?): Boolean =
        other is AnswerUiModel &&
            idOption == other.idOption &&
            text == other.text &&
            isFreeText == other.isFreeText &&
            isSelected == other.isSelected
            && value == other.value

    override fun hashCode(): Int {
        var result = idOption
        result = 31 * result + text.hashCode()
        result = 31 * result + isFreeText.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + isSelected.hashCode()
        return result
    }
}
