package com.discut.manga.ui.reader.viewer.horizontal

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.DirectionalViewPager
import com.discut.manga.ui.reader.GestureDetectorWithLongTap

@SuppressLint("ViewConstructor")
open class HorizontalPagesView(context: Context, isHorizontal: Boolean = true) :
    DirectionalViewPager(context, isHorizontal) {

    private val gestureDetector = GestureDetectorWithLongTap(context, GestureListener())

    var signalTapListener: ((MotionEvent) -> Unit)? = null


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val event = super.dispatchTouchEvent(ev)
        gestureDetector.onTouchEvent(ev)
        return event
    }

    inner class GestureListener : GestureDetectorWithLongTap.Listener() {
        override fun onSingleTapConfirmed(ev: MotionEvent): Boolean {
            signalTapListener?.invoke(ev)
            return false
        }

    }
}