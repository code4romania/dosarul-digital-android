package ro.code4.casefile.ui.section.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ro.code4.casefile.R
import ro.code4.casefile.databinding.ItemPatientBinding
import ro.code4.casefile.ui.patient.model.PatientUiModel

class PatientsAdapter(
    val patients: MutableList<PatientUiModel>,
    val patientDetailsCallback: (patientId: Int) -> Unit,
    val fillFormCallback: (patientId: Int) -> Unit
) : RecyclerView.Adapter<PatientsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientsViewHolder {
        val binding = DataBindingUtil.inflate<ItemPatientBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_patient,
            parent,
            false
        )
        return PatientsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    override fun onBindViewHolder(holder: PatientsViewHolder, position: Int) {
        val patientCardUiModel = patients[position]
        holder.binding.patientUiModel = patientCardUiModel
        holder.binding.fillFormButton.setOnClickListener {
            fillFormCallback(patientCardUiModel.id)
        }
        holder.binding.patientDetailsClickInterceptor.setOnClickListener {
            patientDetailsCallback(patientCardUiModel.id)
        }
    }
}
