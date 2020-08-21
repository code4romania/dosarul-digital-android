package ro.code4.casefile.ui.patient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ro.code4.casefile.R
import ro.code4.casefile.databinding.ItemPatientFormHistoryBinding
import ro.code4.casefile.ui.patient.model.FormHistoryUiModel

class PatientFormHistoryAdapter(
    val formHistoryItems: MutableList<FormHistoryUiModel>,
    private val selectedFormListener: (FormHistoryUiModel) -> Unit
) : RecyclerView.Adapter<PatientFormHistoryAdapter.PatientFormHistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PatientFormHistoryViewHolder {
        val binding = DataBindingUtil.inflate<ItemPatientFormHistoryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_patient_form_history,
            parent,
            false
        )
        return PatientFormHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return formHistoryItems.size
    }

    override fun onBindViewHolder(holder: PatientFormHistoryViewHolder, position: Int) {
        holder.bind(formHistoryItems[position])
        holder.itemView.setOnClickListener {
            selectedFormListener.invoke(formHistoryItems[position])
        }
    }

    class PatientFormHistoryViewHolder(private val binding: ItemPatientFormHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(formHistoryUiModel: FormHistoryUiModel) {
            binding.patientFormHistoryUiModel = formHistoryUiModel
            binding.executePendingBindings()
        }
    }
}
