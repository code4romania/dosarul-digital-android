package ro.code4.casefile.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.adapters.helper.QuestionListItem
import ro.code4.casefile.databinding.ItemQuestionBinding
import ro.code4.casefile.ui.forms.model.QuestionUiModel

class QuestionDelegate(
    private val clickListener: (Int) -> Unit
) : AbsListItemAdapterDelegate<QuestionListItem, ListItem, QuestionDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        )

    override fun isForViewType(
        item: ListItem,
        items: MutableList<ListItem>,
        position: Int
    ): Boolean =
        item is QuestionListItem

    override fun onBindViewHolder(
        item: QuestionListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item.questionUiModel)
        holder.itemView.setOnClickListener { clickListener(item.questionId) }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemQuestionBinding.bind(view)

        fun bind(questionUiModel: QuestionUiModel) {
            binding.questionUiModel = questionUiModel
            binding.executePendingBindings()
        }
    }
}
