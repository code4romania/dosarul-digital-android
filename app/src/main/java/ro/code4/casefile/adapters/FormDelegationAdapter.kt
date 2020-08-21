package ro.code4.casefile.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ro.code4.casefile.adapters.delegates.FormDelegate
import ro.code4.casefile.adapters.helper.FormListItem
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.ui.forms.model.SelectedFormInfo
import java.util.*

class FormDelegationAdapter(
    formClickListener: (SelectedFormInfo) -> Unit,
    beneficiaryId: Int,
    completionDate: Date?
) : AsyncListDifferDelegationAdapter<ListItem>(DIFF_CALLBACK) {
    init {
        delegatesManager.addDelegate(FormDelegate(formClickListener, beneficiaryId, completionDate))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is FormListItem && newItem is FormListItem -> oldItem.formItemUiModel.id == newItem.formItemUiModel.id
                    else -> false
                }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is FormListItem && newItem is FormListItem -> oldItem.formItemUiModel.equals(newItem.formItemUiModel)
                    else -> false
                }
        }
    }
}
