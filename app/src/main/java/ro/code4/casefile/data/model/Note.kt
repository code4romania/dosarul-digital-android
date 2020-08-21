package ro.code4.casefile.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.ui.patient.model.NoteUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    tableName = "note",
    indices = [Index(value = ["beneficiaryId", "questionId"], unique = false)]
)
class Note {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Expose
    var attachmentPath: String? = null

    @Expose
    var text: String? = null

    @Expose
    var questionId: Int? = null

    @Expose
    var lastModified: Date = Date()

    @Expose
    var beneficiaryId: Int? = null

    @Expose
    var formId: Int? = null

    var synced = false

    override fun equals(other: Any?): Boolean =
        other is Note &&
            other.id == id &&
            other.attachmentPath == attachmentPath &&
            other.questionId == questionId &&
            other.lastModified == lastModified &&
            other.synced == synced &&
            other.text == text

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (attachmentPath?.hashCode() ?: 0)
        result = 31 * result + (questionId ?: 0)
        result = 31 * result + lastModified.hashCode()
        result = 31 * result + synced.hashCode()
        return result
    }
}

fun Note.toUiModel(): NoteUiModel {
    val simpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT_SIMPLE, Locale.US)
    return NoteUiModel(id, text, simpleDateFormat.format(lastModified.time))
}
