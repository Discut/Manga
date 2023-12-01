package com.discut.manga.ui.reader.viewer.container

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.discut.manga.R
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.ui.reader.ReaderViewModel
import com.discut.manga.ui.reader.adapter.RecyclerPagesViewAdapter
import com.discut.manga.ui.reader.viewer.RecyclerPagesView

class VerticalPagesContainer(
    readerViewModel: ReaderViewModel,
    readerActivity: ReaderActivity
) : PagesContainer<RecyclerPagesViewAdapter, RecyclerPagesView>() {

    private var container: RecyclerPagesView = createContainer(readerActivity)

    private lateinit var _adapter: RecyclerPagesViewAdapter
    override var adapter: RecyclerPagesViewAdapter
        get() = _adapter
        set(value) {
            container.adapter = value
            _adapter = value
        }
    override var isVisible: Boolean
        get() = container.isVisible
        set(value) {
            container.isVisible = value
        }

    init {
        container.apply {
            isVisible = false
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFocusable = false
            itemAnimator = null
            layoutManager = LinearLayoutManager(context).apply {
                isItemPrefetchEnabled = false
            }
        }
        readerActivity.findViewById<FrameLayout>(R.id.page_container)
            .addView(container)
    }

    override fun createContainer(context: Context): RecyclerPagesView = RecyclerPagesView(context)

}