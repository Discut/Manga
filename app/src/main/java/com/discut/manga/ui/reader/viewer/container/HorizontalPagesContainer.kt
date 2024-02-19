package com.discut.manga.ui.reader.viewer.container

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.discut.manga.R
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.ui.reader.ReaderViewModel
import com.discut.manga.ui.reader.adapter.PageViewerAdapter
import com.discut.manga.ui.reader.viewer.PagesView

class HorizontalPagesContainer(
    readerViewModel: ReaderViewModel,
    readerActivity: ReaderActivity
) : PagesContainer {
    private var pageViewContainer: PagesView = createContainer(readerActivity)

    private lateinit var _adapter: PageViewerAdapter

    var adapter: PageViewerAdapter
        set(a) {
            _adapter = a
            pageViewContainer.adapter = _adapter
        }
        get() = _adapter

    override var isVisible: Boolean
        set(value) {
            pageViewContainer.isVisible = value
        }
        get() = pageViewContainer.isVisible

    init {
        pageViewContainer.apply {
            isVisible = false
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFocusable = false
            offscreenPageLimit = 1
        }
        readerActivity.findViewById<FrameLayout>(R.id.page_container)
            .addView(pageViewContainer)
    }

    fun createContainer(context: Context): PagesView = PagesView(context, true)
    override fun onScrolled(index: Int?) {
        TODO("Not yet implemented")
    }

    override fun moveToPage(position: Int) {
        TODO("Not yet implemented")
    }

}