package ro.code4.casefile.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_form_patient.*
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.AddNoteListItem
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.helper.highlight

class AddNoteDelegate(
    private val clickListener: () -> Unit
) : AbsListItemAdapterDelegate<AddNoteListItem, ListItem, AddNoteDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_form_patient, parent, false),
            clickListener
        )

    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean =
        item is AddNoteListItem

    override fun onBindViewHolder(
        item: AddNoteListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind()
    }

    class ViewHolder(override val containerView: View, clickListener: () -> Unit) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        init {
            containerView.setOnClickListener { clickListener() }
        }

        fun bind() {
            formProgress.visibility = View.INVISIBLE
            formQuestionsAnswered.visibility = View.INVISIBLE
            with(formTitle) {
                text = context.highlight(context.getString(R.string.form_notes))
            }
        }
    }
}
