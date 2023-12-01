package com.discut.manga.ui.reader.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.discut.manga.ui.reader.viewer.PageView
import com.discut.manga.ui.reader.viewer.RecyclerPageHolder
import com.discut.manga.ui.reader.viewer.domain.ReaderPage

class RecyclerPagesViewAdapter(private val context: Context, readerPages: List<ReaderPage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), IPagesViewAdapter {

    var readerPages: MutableList<ReaderPage> = readerPages.toMutableList()
        private set


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PAGE_VIEW -> {
                val pageView = PageView(parent.context)
                RecyclerPageHolder(parent.context, pageView)
            }

            TRANSITION_VIEW -> {
                val pageView = PageView(parent.context)
                RecyclerPageHolder(parent.context, pageView)
            }

            else -> {
                throw IllegalArgumentException("Unknown view type")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = readerPages.getOrNull(position)) {
            is ReaderPage.ChapterPage -> PAGE_VIEW
            is ReaderPage.ChapterTransition -> TRANSITION_VIEW
            else -> throw IllegalStateException("Unknown view type ${item?.javaClass}")
        }
    }

    override fun getItemCount(): Int {
        return readerPages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecyclerPageHolder -> {
                (readerPages[position] as? ReaderPage.ChapterPage)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    override fun createPageView(container: ViewGroup, position: Int): View =
        throw IllegalStateException("No use")

    override fun destroyPageView(container: ViewGroup, position: Int, view: View) {
        TODO("Not yet implemented")
    }
}

private const val PAGE_VIEW = 0
private const val TRANSITION_VIEW = 1