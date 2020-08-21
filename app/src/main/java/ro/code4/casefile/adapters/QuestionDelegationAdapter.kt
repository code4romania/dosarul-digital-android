package ro.code4.casefile.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ro.code4.casefile.adapters.delegates.QuestionDelegate
import ro.code4.casefile.adapters.delegates.QuestionSectionDelegate
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.adapters.helper.QuestionListItem
import ro.code4.casefile.adapters.helper.QuestionSectionListItem

class QuestionDelegationAdapter(
    questionClickListener: (Int) -> Unit
) : AsyncListDifferDelegationAdapter<ListItem>(DIFF_CALLBACK) {
    init {
        delegatesManager
            .addDelegate(QuestionSectionDelegate())
            .addDelegate(QuestionDelegate(questionClickListener))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is QuestionListItem && newItem is QuestionListItem -> oldItem.questionId == newItem.questionId
                    oldItem is QuestionSectionListItem && newItem is QuestionSectionListItem -> oldItem.sectionUiModel == newItem.sectionUiModel
                    else -> false
                }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is QuestionListItem && newItem is QuestionListItem -> oldItem.questionUiModel == newItem.questionUiModel
                    oldItem is QuestionSectionListItem && newItem is QuestionSectionListItem -> oldItem.sectionUiModel == newItem.sectionUiModel
                    else -> false
                }
        }
    }
}
