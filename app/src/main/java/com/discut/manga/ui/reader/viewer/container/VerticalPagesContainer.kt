package com.discut.manga.ui.reader.viewer.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.WebtoonLayoutManager
import com.discut.manga.R
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.ui.reader.ReaderViewModel
import com.discut.manga.ui.reader.adapter.RecyclerPagesViewAdapter
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.navigation.BaseReaderClickNavigation.NavigationRegion
import com.discut.manga.ui.reader.navigation.LShapeNavigation
import com.discut.manga.ui.reader.utils.transformToAction
import com.discut.manga.ui.reader.viewer.RecyclerPagesView
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import com.discut.manga.ui.reader.viewer.scrollDown
import com.discut.manga.ui.reader.viewer.scrollUp

@SuppressLint("ViewConstructor")
class VerticalPagesContainer(
    private val readerViewModel: ReaderViewModel,
    private val readerActivity: ReaderActivity
) : PagesContainer, FrameLayout(readerActivity) {

    private var container: RecyclerPagesView = createContainer(readerActivity)

    private var layoutManager: WebtoonLayoutManager = WebtoonLayoutManager(readerActivity)


    private lateinit var _adapter: RecyclerPagesViewAdapter
    var adapter: RecyclerPagesViewAdapter
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
            layoutManager = this@VerticalPagesContainer.layoutManager

            signalTapListener = {
                val position = PointF(it.x / width, it.y / height)
                when (position.transformToAction(LShapeNavigation())) {
                    NavigationRegion.MENU -> readerViewModel.sendEvent(
                        ReaderActivityEvent.ReaderNavigationMenuVisibleChange(
                            !readerViewModel.uiState.value.isMenuShow
                        )
                    )

                    NavigationRegion.NEXT, NavigationRegion.RIGHT -> {
                        scrollDown(focusLength)
                        changeMenuVisible(false)
                    }

                    NavigationRegion.PREV, NavigationRegion.LEFT -> {
                        scrollUp(focusLength)
                        changeMenuVisible(false)
                    }
                }
            }
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        onScrolled()
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            //isSliderScrolling = false
                            changeMenuVisible(false)
                        }
                    }
                }
            )
            /*setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (abs(oldScrollY - scrollY) == 0) {
                    return@setOnScrollChangeListener
                }
                val visible = readerViewModel.uiState.value.isMenuShow
                if (visible) {
                    readerViewModel.sendEvent(
                        ReaderActivityEvent.ReaderNavigationMenuVisibleChange(
                            false
                        )
                    )
                }
            }*/
        }
        addView(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        readerActivity.findViewById<FrameLayout>(R.id.page_container)
            .addView(this)
    }

    /**
     * Focus on the screen length
     */
    private val focusLength = resources.displayMetrics.heightPixels * 3 / 4

    fun createContainer(context: Context): RecyclerPagesView = RecyclerPagesView(context)
    override fun onScrolled(index: Int?) {
        val position = index ?: layoutManager.findLastEndVisibleItemPosition()
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
        adapter.readerPages.getOrNull(position)?.let {
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }


    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            //recycler?.onScaleBegin()
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            container.onScale(detector.scaleFactor)
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            //recycler?.onScaleEnd()
        }
    }

}



