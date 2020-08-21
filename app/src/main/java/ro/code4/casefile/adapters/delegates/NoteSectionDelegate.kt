package ro.code4.casefile.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_section.*
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.adapters.helper.NoteSectionListItem

class NoteSectionDelegate : AbsListItemAdapterDelegate<NoteSectionListItem, ListItem, NoteSectionDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_section,
                parent,
                false
            )
        )

    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean =
        item is NoteSectionListItem

    override fun onBindViewHolder(
        item: NoteSectionListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        lateinit var item: NoteSectionListItem

        fun bind(sectionListItem: NoteSectionListItem) {
            item = sectionListItem

            sectionName.text = sectionName.context.getString(
                item.titleResourceId, *item.formatArgs
            )
        }
    }
}
