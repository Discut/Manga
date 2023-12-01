package com.discut.manga.ui.reader.viewer

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.recyclerview.widget.RecyclerView

class RecyclerPagesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RecyclerView(context, attrs, defStyle) {

    private var isZooming = false
    private var atLastPosition = false
    private var atFirstPosition = false
    private var halfWidth = 0
    private var halfHeight = 0
    private var originalHeight = 0
    private var heightSet = false
    private var firstVisibleItemPosition = 0
    private var lastVisibleItemPosition = 0
    private var currentScale = DEFAULT_RATE

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        halfWidth = MeasureSpec.getSize(widthSpec) / 2
        halfHeight = MeasureSpec.getSize(heightSpec) / 2
        if (!heightSet) {
            //originalHeight = resources.displayMetrics.heightPixels
            updateScreenHeight()
            heightSet = true
        }
        super.onMeasure(widthSpec, heightSpec)
    }

    private fun setScaleRate(rate: Float) {
        scaleX = rate
        scaleY = rate
    }

    fun onScale(scaleFactor: Float) {
        currentScale *= scaleFactor
        currentScale = currentScale.coerceIn(
            MIN_RATE,
            MAX_SCALE_RATE,
        )

        setScaleRate(currentScale)

        layoutParams.height = if (currentScale < 1) {
            (originalHeight / currentScale).toInt()
        } else {
            originalHeight
        }
        halfHeight = layoutParams.height / 2

        if (currentScale != DEFAULT_RATE) {
            x = getPositionX(x)
            y = getPositionY(y)
        } else {
            x = 0f
            y = 0f
        }

        requestLayout()
    }

    private fun getPositionX(positionX: Float): Float {
        if (currentScale < 1) {
            return 0f
        }
        val maxPositionX = halfWidth * (currentScale - 1)
        return positionX.coerceIn(-maxPositionX, maxPositionX)
    }

    private fun getPositionY(positionY: Float): Float {
        if (currentScale < 1) {
            return (originalHeight / 2 - halfHeight).toFloat()
        }
        val maxPositionY = halfHeight * (currentScale - 1)
        return positionY.coerceIn(-maxPositionY, maxPositionY)
    }

    private fun updateScreenHeight() {
        val heightPixels = resources.displayMetrics.heightPixels
        originalHeight = heightPixels
        (context as? Activity)?.apply {
            window.decorView.setOnApplyWindowInsetsListener { v, insets ->
                val navigationHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    rootWindowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars()).bottom
                } else {
                    insets.stableInsetBottom
                }

                val height = resources.displayMetrics.heightPixels + navigationHeight
                originalHeight = if (height > heightPixels) {
                    height
                } else {
                    heightPixels
                }
                insets
            }
        }
    }
}

internal const val ANIMATOR_DURATION_TIME = 200
internal const val MIN_RATE = 0.5f
internal const val DEFAULT_RATE = 1f
internal const val MAX_SCALE_RATE = 3f