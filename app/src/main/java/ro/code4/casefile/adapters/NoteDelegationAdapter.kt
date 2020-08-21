package ro.code4.casefile.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ro.code4.casefile.adapters.delegates.NoteDelegate
import ro.code4.casefile.adapters.delegates.NoteSectionDelegate
import ro.code4.casefile.adapters.helper.ListItem
import ro.code4.casefile.adapters.helper.NoteListItem
import ro.code4.casefile.adapters.helper.NoteSectionListItem

class NoteDelegationAdapter : AsyncListDifferDelegationAdapter<ListItem>(DIFF_CALLBACK) {
    init {
        delegatesManager
            .addDelegate(NoteSectionDelegate())
            .addDelegate(NoteDelegate())
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is NoteListItem && newItem is NoteListItem -> oldItem.note.id == newItem.note.id
                    oldItem is NoteSectionListItem && newItem is NoteSectionListItem -> oldItem.titleResourceId == newItem.titleResourceId
                    else -> false
                }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is NoteListItem && newItem is NoteListItem -> oldItem.note == newItem.note
                    oldItem is NoteSectionListItem && newItem is NoteSectionListItem -> oldItem.titleResourceId == newItem.titleResourceId && oldItem.formatArgs contentDeepEquals newItem.formatArgs
                    else -> false
                }
        }
    }
}
