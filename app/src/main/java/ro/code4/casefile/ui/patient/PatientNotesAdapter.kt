package ro.code4.casefile.ui.patient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ro.code4.casefile.R
import ro.code4.casefile.databinding.ItemPatientNoteBinding
import ro.code4.casefile.ui.patient.model.NoteUiModel

class PatientNotesAdapter(
    val notes: MutableList<NoteUiModel>,
    private val selectedNoteListener: (Int) -> Unit
) : RecyclerView.Adapter<PatientNotesAdapter.PatientNoteViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PatientNoteViewHolder {
        val binding = DataBindingUtil.inflate<ItemPatientNoteBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_patient_note,
            parent,
            false
        )
        return PatientNoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: PatientNoteViewHolder, position: Int) {
        holder.bind(notes[position])
        holder.itemView.setOnClickListener {
            selectedNoteListener.invoke(notes[position].id)
        }
    }

    class PatientNoteViewHolder(private val binding: ItemPatientNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(noteUiModel: NoteUiModel) {
            binding.noteUiModel = noteUiModel
            binding.executePendingBindings()
        }
    }
}
