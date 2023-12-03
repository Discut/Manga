package com.discut.manga.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout


class ClipChildFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    override fun dispatchDraw(canvas: Canvas) {
        val path = Path()
        val rectF = RectF()
        rectF.set(0F, 0F, width.toFloat(), height.toFloat())
        path.addRect(rectF,Path.Direction.CW)
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
    }
}