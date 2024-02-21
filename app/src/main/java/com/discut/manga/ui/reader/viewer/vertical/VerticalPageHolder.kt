package com.discut.manga.ui.reader.viewer.vertical

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.discut.manga.components.reader.ReaderProgressIndicatorComponent
import com.discut.manga.ui.reader.page.PageView
import com.discut.manga.ui.reader.domain.PageState
import com.discut.manga.ui.reader.domain.ReaderPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class VerticalPageHolder(
    private val context: Context,
    private val content: PageView
) : RecyclerView.ViewHolder(content) {
    private val imageLoadProgress: ReaderProgressIndicatorComponent =
        buildProgressIndicator(context)

    private lateinit var imageLoadProgressContainer: ViewGroup

    private val progressIndicatorLayoutHeight
        get() = context.resources.displayMetrics.heightPixels /*/ 3*/

    private var loadJob: Job? = null

    lateinit var readerPage: ReaderPage.ChapterPage

    init {
        //content.addView(imageLoadProgressContainer)
/*                        content.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )*/
    }

    fun bind(readerPage: ReaderPage.ChapterPage) {
        this.readerPage = readerPage
        loadJob?.cancel()
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
                readerPage.loadUrl()
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

            PageState.LOAD_URL -> {
                imageLoadProgress.show()
            }

            PageState.DOWNLOAD_IMAGE -> {
                imageLoadProgress.show()
                readerPage.progressFlow.collectLatest(imageLoadProgress::setProgress)
            }

            PageState.LOAD_PAGE -> {

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
            progressIndicatorLayoutHeight
        )
        val progress = ReaderProgressIndicatorComponent(context)
        imageLoadProgressContainer.addView(progress)
        return progress
    }

}