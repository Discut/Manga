package com.discut.manga.ui.reader.viewer.vertical

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.discut.manga.ui.reader.viewer.IPagesViewAdapter.PageType.PAGE_VIEW
import com.discut.manga.ui.reader.viewer.IPagesViewAdapter.PageType.TRANSITION_VIEW
import com.discut.manga.ui.reader.page.PageTransitionView
import com.discut.manga.ui.reader.page.PageView
import com.discut.manga.ui.reader.domain.ReaderPage
import com.discut.manga.ui.reader.viewer.IPagesViewAdapter

class VerticalPagesViewAdapter(private val context: Context, readerPages: List<ReaderPage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), IPagesViewAdapter {

    var readerPages: MutableList<ReaderPage> = readerPages.toMutableList()
        private set


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (val view =
            createPageView(parent, IPagesViewAdapter.PageType.entries[viewType])) {
            is PageView -> VerticalPageHolder(context, view)
            is PageTransitionView -> VerticalTransitionHolder(context, view)
            else -> throw IllegalStateException("Unknown view type ${view.javaClass}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = readerPages.getOrNull(position)) {
            is ReaderPage.ChapterPage -> PAGE_VIEW.ordinal
            is ReaderPage.ChapterTransition -> TRANSITION_VIEW.ordinal
            else -> throw IllegalStateException("Unknown view type ${item?.javaClass}")
        }
    }

    override fun getItemCount(): Int {
        return readerPages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VerticalPageHolder -> {
                (readerPages[position] as? ReaderPage.ChapterPage)?.let {
                    holder.bind(it)
                }
            }

            is VerticalTransitionHolder -> {
                (readerPages[position] as? ReaderPage.ChapterTransition)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    override fun createPageView(container: ViewGroup, type: IPagesViewAdapter.PageType): View {
        return when (type) {
            PAGE_VIEW -> PageView(container.context)
            TRANSITION_VIEW -> PageTransitionView(container.context)
        }
    }

    override fun destroyPageView(container: ViewGroup, position: Int, view: View) {
        TODO("Not yet implemented")
    }

    override fun setPages(pages: List<ReaderPage>) {
        val oldItems = readerPages
        val newItems = pages
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                true
        }
        )
        readerPages = newItems.toMutableList()
        diff.dispatchUpdatesTo(this)
    }

}