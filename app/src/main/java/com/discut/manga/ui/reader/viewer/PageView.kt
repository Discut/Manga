package com.discut.manga.ui.reader.viewer

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.discut.manga.ui.util.GLUtil
import java.io.InputStream

open class PageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttrs: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttrs, defStyleRes) {

    private var content: View? = null

    private var config: Config = Config()

    internal fun setupPageContent(image: InputStream, config: Config = Config()) {
        structurePageContent(ImageSource.inputStream(image))
        this.config = config
    }

    internal fun setupPageContent(image: InputStream, configFun: Config.() -> Unit) {
        configFun.invoke(config)
        structurePageContent(ImageSource.inputStream(image))
    }

    internal fun setupPageContent(image: BitmapDrawable, config: Config = Config()) {
        structurePageContent(ImageSource.bitmap(image.bitmap))
        this.config = config
    }

    private fun structurePageContent(image: ImageSource) {
        removeAllViews()
        content = if (config.isWebtoon) {
            WebtoonSubsamplingImageView(context)
        } else {
            SubsamplingScaleImageView(context)
        }
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
        addView(content, MATCH_PARENT, WRAP_CONTENT)
        (content as? SubsamplingScaleImageView)?.apply {
            setDoubleTapZoomDuration(100)
            setMinimumScaleType(config.minimumScaleType)
            setMinimumDpi(1) // Just so that very small image will be fit for initial load
            setCropBorders(config.cropBorders)
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
            setImage(image)
            isVisible = true
        }
    }

    data class Config(
        var zoomDuration: Int = 100,
        var minimumScaleType: Int = SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE,
        var cropBorders: Boolean = false,
        //val zoomStartPosition: ZoomStartPosition = ZoomStartPosition.CENTER,
        var landscapeZoom: Boolean = false,

        //
        var isWebtoon: Boolean = false
    )
}