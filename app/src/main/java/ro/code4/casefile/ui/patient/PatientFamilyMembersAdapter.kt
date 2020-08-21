package ro.code4.casefile.ui.patient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ro.code4.casefile.R
import ro.code4.casefile.databinding.ItemPatientFamilyMemberBinding
import ro.code4.casefile.ui.patient.model.FamilyMemberUiModel

class PatientFamilyMembersAdapter(
    val familyMembers: MutableList<FamilyMemberUiModel>,
    private val selectedFamilyMemberListener: (Int) -> Unit
) : RecyclerView.Adapter<PatientFamilyMembersAdapter.FamilyMemberViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FamilyMemberViewHolder {
        val binding = DataBindingUtil.inflate<ItemPatientFamilyMemberBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_patient_family_member,
            parent,
            false
        )
        return FamilyMemberViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return familyMembers.size
    }

    override fun onBindViewHolder(holder: FamilyMemberViewHolder, position: Int) {
        holder.bind(familyMembers[position])
        holder.itemView.setOnClickListener {
            selectedFamilyMemberListener.invoke(familyMembers[position].id)
        }
    }

    class FamilyMemberViewHolder(private val binding: ItemPatientFamilyMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(familyMemberUiModel: FamilyMemberUiModel) {
            binding.familyMemberUiModel = familyMemberUiModel
            binding.executePendingBindings()
        }
    }
}
