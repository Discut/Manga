package com.discut.manga.ui.reader.page

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class WebtoonSubsamplingImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : SubsamplingScaleImageView(context, attrs) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

}