package com.discut.manga.ui.reader.viewer.container

import android.content.Context
import android.graphics.PointF
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.discut.manga.R
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.ui.reader.ReaderViewModel
import com.discut.manga.ui.reader.adapter.PageViewerAdapter
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.navigation.BaseReaderClickNavigation
import com.discut.manga.ui.reader.navigation.LShapeNavigation
import com.discut.manga.ui.reader.utils.transformToAction
import com.discut.manga.ui.reader.viewer.PagesView
import com.discut.manga.ui.reader.viewer.domain.ReaderPage

class HorizontalPagesContainer(
    private val readerViewModel: ReaderViewModel,
    readerActivity: ReaderActivity
) : PagesContainer {
    private var pageViewContainer: PagesView = createContainer(readerActivity)

    private lateinit var _adapter: PageViewerAdapter

    /**
     * 'pvc' means 'page viewer container'
     */
    private var _pvcContainer: FrameLayout

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

            signalTapListener = {
                val position = PointF(it.x / width, it.y / height)
                when (position.transformToAction(LShapeNavigation())) {
                    BaseReaderClickNavigation.NavigationRegion.MENU -> changeMenuVisible(!readerViewModel.uiState.value.isMenuShow)

                    BaseReaderClickNavigation.NavigationRegion.NEXT, BaseReaderClickNavigation.NavigationRegion.RIGHT -> {
                        moveToPage(currentItem + 1)
                        changeMenuVisible(false)
                    }

                    BaseReaderClickNavigation.NavigationRegion.PREV, BaseReaderClickNavigation.NavigationRegion.LEFT -> {
                        moveToPage(currentItem - 1)
                        changeMenuVisible(false)
                    }
                }
            }

            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                onScrolled()
            }
        }
        _pvcContainer = readerActivity.findViewById<FrameLayout>(R.id.page_container)
            .apply {
                addView(pageViewContainer)
            }

    }

    fun createContainer(context: Context): PagesView = PagesView(context, true)
    override fun onScrolled(index: Int?) {
        changeMenuVisible(false)
        val position = index ?: pageViewContainer.currentItem
        val page = adapter.readerPages.getOrNull(position) ?: return
        when (page) {
            is ReaderPage.ChapterPage -> {
                readerViewModel.sendEvent(
                    ReaderActivityEvent.PageSelected(position)
                )
            }

            is ReaderPage.ChapterTransition -> TODO()
        }
    }

    override fun moveToPage(position: Int) {
        pageViewContainer.currentItem = position
    }

    override fun destroy() {
        _pvcContainer.removeAllViews()
    }

    override fun setPages(pages: List<ReaderPage>) {
        adapter.setPages(pages)
    }


    private fun changeMenuVisible(visible: Boolean) {
        if (visible == readerViewModel.uiState.value.isMenuShow) {
            return
        }
        readerViewModel.sendEvent(
            ReaderActivityEvent.ReaderNavigationMenuVisibleChange(
                visible
            )
        )

    }
}