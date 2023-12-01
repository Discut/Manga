package com.discut.manga.ui.reader.viewer

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.discut.manga.components.reader.ReaderProgressIndicatorComponent
import com.discut.manga.ui.reader.viewer.domain.PageState
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class RecyclerPageHolder(
    context: Context,
    private val content: PageView
) : RecyclerView.ViewHolder(content) {
    private val imageLoadProgress: ReaderProgressIndicatorComponent =
        buildProgressIndicator(context)

    private lateinit var imageLoadProgressContainer: ViewGroup

    private var loadJob: Job? = null

    lateinit var readerPage: ReaderPage.ChapterPage

    init {
        //content.addView(imageLoadProgressContainer)
        /*                content.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )*/
    }

    fun bind(readerPage: ReaderPage.ChapterPage) {
        this.readerPage = readerPage
        loadJob = MainScope().launch {
            loadPage()
        }
        adaptLayout()
    }

    private fun adaptLayout() {
        content.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
/*            if (!viewer.isContinuous) {
                bottomMargin = 15.dpToPx
            }*/

            val margin = Resources.getSystem().displayMetrics.widthPixels * (0 / 100f)
            marginEnd = margin.toInt()
            marginStart = margin.toInt()
        }
    }

    private suspend fun loadPage() {
        supervisorScope {
            launch(Dispatchers.IO) {
                readerPage.loadPage() // 加载stream
            }
            readerPage.stateFlow.collectLatest(::subscribeState)
        }
    }

    private suspend fun subscribeState(state: PageState) {
        when (state) {
            PageState.WAIT -> {
                imageLoadProgress.show()
            }

            PageState.LOAD_PAGE -> {

            }

            PageState.DOWNLOAD_IMAGE -> {

            }

            PageState.READY -> {
                readerPage.streamGetter?.invoke()?.let { stream ->
                    content.setupPageContent(stream) {
                        minimumScaleType =
                            SubsamplingScaleImageView.SCALE_TYPE_FIT_WIDTH
                        isWebtoon = true
                    }
                }
                imageLoadProgress.hide()
                content.removeView(imageLoadProgressContainer)
            }

            PageState.ERROR -> {

            }
        }
    }

    private fun buildProgressIndicator(context: Context): ReaderProgressIndicatorComponent {
        imageLoadProgressContainer = FrameLayout(context)
        content.addView(
            imageLoadProgressContainer,
            ViewGroup.LayoutParams.MATCH_PARENT,
            content.resources.displayMetrics.heightPixels / 3
        )
        val progress = ReaderProgressIndicatorComponent(context)
        imageLoadProgressContainer.addView(progress)
        return progress
    }

}