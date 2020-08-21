package ro.code4.casefile.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.FormListItem
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.databinding.ItemFormPatientBinding
import ro.code4.casefile.ui.forms.model.FormItemUiModel
import ro.code4.casefile.ui.forms.model.SelectedFormInfo
import java.util.*

class FormDelegate(
    private val clickListener: (SelectedFormInfo) -> Unit,
    private val beneficiaryId: Int,
    private val completionDate: Date?
) : AbsListItemAdapterDelegate<FormListItem, ListItem, FormDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_form_patient, parent, false)
        )
    }

    override fun isForViewType(
        item: ListItem,
        items: MutableList<ListItem>,
        position: Int
    ): Boolean =
        item is FormListItem

    override fun onBindViewHolder(
        item: FormListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        val formItemInfo = item.formItemUiModel
        holder.bind(formItemInfo)
        holder.itemView.setOnClickListener {
            clickListener(
                SelectedFormInfo(
                    formItemInfo.id,
                    formItemInfo.title,
                    beneficiaryId,
                    completionDate
                )
            )
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemFormPatientBinding.bind(view)

        fun bind(formItemUiModel: FormItemUiModel) {
            binding.formItemInfo = formItemUiModel
            binding.executePendingBindings()
        }
    }
}
