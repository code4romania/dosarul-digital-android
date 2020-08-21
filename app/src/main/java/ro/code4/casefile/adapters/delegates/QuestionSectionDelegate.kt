package ro.code4.casefile.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.adapters.helper.QuestionSectionListItem
import ro.code4.casefile.databinding.ItemSectionQuestionBinding

class QuestionSectionDelegate :
    AbsListItemAdapterDelegate<QuestionSectionListItem, ListItem, QuestionSectionDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemSectionQuestionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_section_question,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun isForViewType(
        item: ListItem,
        items: MutableList<ListItem>,
        position: Int
    ): Boolean =
        item is QuestionSectionListItem

    override fun onBindViewHolder(
        item: QuestionSectionListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(val binding: ItemSectionQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var item: QuestionSectionListItem

        fun bind(sectionListItem: QuestionSectionListItem) {
            binding.sectionUiModel = sectionListItem.sectionUiModel
            binding.executePendingBindings()
        }
    }
}
