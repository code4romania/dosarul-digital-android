package ro.code4.casefile.ui.forms.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ro.code4.casefile.R
import ro.code4.casefile.databinding.ItemFormSelectableBinding
import ro.code4.casefile.ui.forms.model.FormSelectionItemUiModel
import kotlin.reflect.KFunction2

class FormsSelectionAdapter(
    var formsSelectionItems: MutableList<FormSelectionItemUiModel>,
    private val selectForm: KFunction2<Int, Boolean, Unit>
) : RecyclerView.Adapter<FormsSelectionAdapter.FormSelectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormSelectionViewHolder {
        val binding = DataBindingUtil.inflate<ItemFormSelectableBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_form_selectable,
            parent,
            false
        )
        return FormSelectionViewHolder(binding)
    }

    override fun getItemCount() = formsSelectionItems.size

    override fun onBindViewHolder(holder: FormSelectionViewHolder, position: Int) {
        val uiModel = formsSelectionItems[position]
        holder.binding.setFormTitle(uiModel.title)
        holder.binding.formTitle.isSelected = uiModel.isSelected
        holder.binding.root.setOnClickListener {
            uiModel.isSelected = !uiModel.isSelected
            selectForm.invoke(uiModel.id, uiModel.isSelected)
            notifyItemChanged(position, uiModel)
        }
    }

    class FormSelectionViewHolder(val binding: ItemFormSelectableBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}
