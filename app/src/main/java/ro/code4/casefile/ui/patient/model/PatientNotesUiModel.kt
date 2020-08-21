package ro.code4.casefile.ui.patient.model

import android.view.View

data class PatientNotesUiModel(
    val notes: List<NoteUiModel>,
    val notesListVisibility: Int = View.GONE,
    val noNotesLayoutVisibility: Int = View.VISIBLE
)
