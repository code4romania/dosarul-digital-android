package ro.code4.casefile.ui.forms.model

data class FormItemUiModel(
    val id: Int,
    val title: String,
    val titleWithCode: CharSequence,
    val questionsAnswered: String,
    val progress: Int,
    val progressMax: Int,
    val progressVisibility: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FormItemUiModel

        if (id != other.id) return false
        if (title != other.title) return false
        if (titleWithCode.toString() != other.titleWithCode.toString()) return false
        if (questionsAnswered != other.questionsAnswered) return false
        if (progress != other.progress) return false
        if (progressMax != other.progressMax) return false
        if (progressVisibility != other.progressVisibility) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + titleWithCode.hashCode()
        result = 31 * result + questionsAnswered.hashCode()
        result = 31 * result + progress
        result = 31 * result + progressMax
        result = 31 * result + progressVisibility
        return result
    }
}
