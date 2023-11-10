package com.discut.manga.ui.reader.viewer

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.discut.manga.ui.util.GLUtil
import java.io.InputStream

open class PageImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttrs: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttrs, defStyleRes) {

    private var content: View? = null

    private fun setupPageContent(image: InputStream) {
        structurePageContent(ImageSource.inputStream(image))
    }

    private fun setupPageContent(image: BitmapDrawable) {
        structurePageContent(ImageSource.bitmap(image.bitmap))
    }
    private fun structurePageContent(image: ImageSource) {
        content =
            SubsamplingScaleImageView(context)
                .apply {
                    setMaxTileSize(GLUtil.maxTextureSize)
                    setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                    setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE)
                    setMinimumTileDpi(180)
                    setOnStateChangedListener(
                        object : SubsamplingScaleImageView.OnStateChangedListener {
                            override fun onScaleChanged(newScale: Float, origin: Int) {
                                //this@ReaderPageImageView.onScaleChanged(newScale)
                            }

                            override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                                // Not used
                            }
                        },
                    )
                    setOnClickListener { /*this@ReaderPageImageView.onViewClicked()*/ }
                }
        addView(content, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (content as? SubsamplingScaleImageView)?.apply {
            setDoubleTapZoomDuration(100)
            setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
            setMinimumDpi(1) // Just so that very small image will be fit for initial load
            setCropBorders(false)
            setOnImageEventListener(
                object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
                    override fun onReady() {
                        /*setupZoom(config)
                        if (isVisibleOnScreen()) landscapeZoom(true)
                        this@ReaderPageImageView.onImageLoaded()*/
                    }

                    override fun onImageLoadError(e: Exception) {
                        /* this@ReaderPageImageView.onImageLoadError()*/
                    }
                },
            )
        }
    }
}