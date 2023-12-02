package com.discut.manga.ui.reader.viewer.container

import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.discut.manga.R
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.ui.reader.ReaderViewModel
import com.discut.manga.ui.reader.adapter.RecyclerPagesViewAdapter
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.navigation.BaseReaderClickNavigation.NavigationRegion
import com.discut.manga.ui.reader.navigation.LShapeNavigation
import com.discut.manga.ui.reader.utils.transformToAction
import com.discut.manga.ui.reader.viewer.RecyclerPagesView
import com.discut.manga.ui.reader.viewer.scrollDown
import com.discut.manga.ui.reader.viewer.scrollUp
import kotlin.math.abs

class VerticalPagesContainer(
    readerViewModel: ReaderViewModel,
    readerActivity: ReaderActivity
) : PagesContainer<RecyclerPagesViewAdapter, RecyclerPagesView>, FrameLayout(readerActivity) {

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

            signalTapListener = {
                val position = PointF(it.x / width, it.y / height)
                when (position.transformToAction(LShapeNavigation())) {
                    NavigationRegion.MENU -> readerViewModel.sendEvent(
                        ReaderActivityEvent.ReaderNavigationMenuVisibleChange(
                            !readerViewModel.uiState.value.isMenuShow
                        )
                    )

                    NavigationRegion.NEXT, NavigationRegion.RIGHT -> scrollDown(focusLength)
                    NavigationRegion.PREV, NavigationRegion.LEFT -> scrollUp(focusLength)
                }
            }
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
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
            }
        }
        addView(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        readerActivity.findViewById<FrameLayout>(R.id.page_container)
            .addView(this)
    }

    /**
     * Focus on the screen length
     */
    private val focusLength = resources.displayMetrics.heightPixels * 3 / 4

    override fun createContainer(context: Context): RecyclerPagesView = RecyclerPagesView(context)

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


