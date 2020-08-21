package ro.code4.casefile.ui.notes

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.adapters.helper.NoteListItem
import ro.code4.casefile.adapters.helper.NoteSectionListItem
import ro.code4.casefile.data.model.Note
import ro.code4.casefile.data.model.toUiModel
import ro.code4.casefile.helper.FileUtils
import ro.code4.casefile.helper.SingleLiveEvent
import ro.code4.casefile.ui.base.BaseFormViewModel
import ro.code4.casefile.ui.patient.model.NoteUiModel
import java.io.File

class NoteViewModel : BaseFormViewModel() {

    private val app: Application by inject()
    private val notesLiveData = MutableLiveData<ArrayList<ListItem>>()
    private val editNoteLiveData = MutableLiveData<NoteUiModel>()
    private val fileNameLiveData = MutableLiveData<String>()
    private val submitCompletedLiveData = SingleLiveEvent<Void>()
    private var noteFile: File? = null

    companion object {
        @JvmStatic
        val TAG = NoteViewModel::class.java.simpleName
    }

    fun notes(): LiveData<ArrayList<ListItem>> = notesLiveData
    fun editNote(): LiveData<NoteUiModel> = editNoteLiveData
    fun fileName(): LiveData<String> = fileNameLiveData
    fun submitCompleted(): SingleLiveEvent<Void> = submitCompletedLiveData
    private var selectedQuestionId: Int? = null
    private var selectedBeneficiaryId: Int? = null
    private var selectedFormId: Int? = null
    private var selectedEditNoteId: Int? = null

    fun setData(questionId: Int?, formId:  Int?, beneficiaryId: Int?, editNoteId: Int?) {
        selectedQuestionId = questionId
        selectedBeneficiaryId = beneficiaryId
        selectedFormId = formId
        selectedEditNoteId = editNoteId

        if (questionId != null && formId != null && beneficiaryId != null) {
            getQuestionNotes(questionId, formId, beneficiaryId)
        }
        if (editNoteId != null) {
            getNote(editNoteId)
        }
    }

    private fun getNote(noteId: Int) {
        disposables.add(
            repository.getNoteDetails(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    note?.let {
                        editNoteLiveData.postValue(it.toUiModel())
                    }
                }
        )
    }

    private fun getQuestionNotes(questionId: Int, formId: Int, beneficiaryId: Int) {
        disposables.add(
            repository.getQuestionNotes(beneficiaryId, formId, questionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { questionNotes ->
                    processList(questionNotes)
                }
        )
    }

    private fun processList(notes: List<Note>) {
        if (notes.isNotEmpty()) {
            val list = ArrayList<ListItem>(notes.size + 1)
            list.add(NoteSectionListItem(R.string.notes_history))
            list.addAll(notes.map { NoteListItem(it) })
            notesLiveData.postValue(list)
        }
    }

    @SuppressLint("CheckResult")
    fun submit(text: String) {
        val note = Note()
        selectedEditNoteId?.let {
            note.id = it
        }
        note.questionId = selectedQuestionId
        note.beneficiaryId = selectedBeneficiaryId
        note.formId = selectedFormId
        note.text = text
        note.attachmentPath = noteFile?.absolutePath
        repository.saveNote(note).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {},
                {
                    Log.d(TAG, it.toString())
                })
        // Writing to database is successful and we don't really need the result of the network call
        submitCompletedLiveData.call()
    }

    fun getMediaFromGallery(uri: Uri?) {
        uri?.let {
            val filePath = FileUtils.getPath(app, it)
            if (filePath != null) {
                val file = File(filePath)
                fileNameLiveData.postValue(file.name)
                noteFile = file
            } else {
                messageIdToastLiveData.postValue(app.getString(R.string.error_permission_external_storage))
            }
        }
    }

    fun addFile(file: File?) {
        noteFile = file
    }

    fun addMediaToGallery() {
        fileNameLiveData.postValue(noteFile?.name)
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(noteFile)
        mediaScanIntent.data = contentUri
        app.sendBroadcast(mediaScanIntent)
    }
}
