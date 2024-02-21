package com.discut.manga.ui.reader.viewer.horizontal

import android.content.Context
import android.graphics.PointF
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.discut.manga.R
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.ui.reader.ReaderViewModel
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.domain.ReaderChapter
import com.discut.manga.ui.reader.domain.ReaderPage
import com.discut.manga.ui.reader.navigation.BaseReaderClickNavigation
import com.discut.manga.ui.reader.navigation.LShapeNavigation
import com.discut.manga.ui.reader.utils.getIndexOfRealFirst
import com.discut.manga.ui.reader.utils.getRealPosition
import com.discut.manga.ui.reader.utils.transformToAction
import com.discut.manga.ui.reader.viewer.PagesContainer

class HorizontalPagesContainer(
    private val readerViewModel: ReaderViewModel,
    readerActivity: ReaderActivity
) : PagesContainer {
    private var pageViewContainer: HorizontalPagesView = createContainer(readerActivity)

    private lateinit var _adapter: HorizontalPageViewerAdapter

    private var waitSwitch: ReaderChapter? = null

    private var awaitingIdlePages: List<ReaderPage>? = null

    private var isIdle = true
        set(value) {
            field = value
            if (value) {
                val prevPosition = pageViewContainer.currentItem
                awaitingIdlePages?.let { pages ->
                    setPagesInternal(pages)
                    awaitingIdlePages = null
                    if (prevPosition == 1) {
                        pageViewContainer.setCurrentItem(pages.size - 4, false)
                    } else {
                        pageViewContainer.setCurrentItem(3, false)
                    }
                    /*if (pages.currChapter.pages?.size == 1) {
                        adapter.nextTransition?.to?.let(activity::requestPreloadChapter)
                    }*/
                }
            }
        }

    /**
     * 'pvc' means 'page viewer container'
     */
    private var _pvcContainer: FrameLayout

    var adapter: HorizontalPageViewerAdapter
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

                    else -> {}
                }
            }

            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                onScrolled()
            }

            addOnPageChangeListener(object :
                ViewPager.SimpleOnPageChangeListener() {
                override fun onPageScrollStateChanged(state: Int) {
                    isIdle = state == ViewPager.SCROLL_STATE_IDLE
                }
            })
        }
        _pvcContainer = readerActivity.findViewById<FrameLayout>(R.id.page_container)
            .apply {
                addView(pageViewContainer)
            }

    }

    fun createContainer(context: Context): HorizontalPagesView = HorizontalPagesView(context, true)
    override fun onScrolled(index: Int?) {
        changeMenuVisible(false)
        val position = index ?: pageViewContainer.currentItem
        val page = adapter.readerPages.getOrNull(position) ?: return
        when (page) {
            is ReaderPage.ChapterPage -> {
                readerViewModel.sendEvent(
                    ReaderActivityEvent.PageSelected(adapter.readerPages.getRealPosition(position))
                )

                val state = readerViewModel.uiState.value.currentChapters?.currReaderChapter?.state
                if (state is ReaderChapter.State.Loaded) {
                    if (state.pages.contains(page).not() && waitSwitch != null) {
                        readerViewModel.sendEvent(
                            ReaderActivityEvent.SwitchToChapter(waitSwitch!!)
                        )
                    } else {
                        waitSwitch = null
                    }
                }
            }

            is ReaderPage.ChapterTransition -> {
                waitSwitch =
                    if (readerViewModel.uiState.value.currentChapters?.currReaderChapter == page.currChapter) {
                        page.prevChapter
                    } else page.currChapter
            }

            else -> {}
        }
    }

    override fun moveToPage(position: Int) {
        pageViewContainer.currentItem = position
    }

    override fun destroy() {
        _pvcContainer.removeAllViews()
    }

    override fun setPages(pages: List<ReaderPage>) {
        if (isIdle) {
            setPagesInternal(pages)
        } else {
            awaitingIdlePages = pages
        }
    }

    private fun setPagesInternal(pages: List<ReaderPage>) {
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