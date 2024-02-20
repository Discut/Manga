package com.discut.manga.ui.reader.viewer

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.discut.manga.components.reader.ReaderProgressIndicatorComponent
import com.discut.manga.ui.reader.viewer.domain.PageState
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@SuppressLint("ViewConstructor")
class ChapterPageHolder(
    context: Context,
    //private val readerPage: ReaderPage.ChapterPage,
    attrs: AttributeSet? = null,
) : PageView(context, attrs),IPageView {

    private val imageLoadProgress: ReaderProgressIndicatorComponent =
        ReaderProgressIndicatorComponent(context)

    private var loadJob: Job? = null

    private lateinit var readerPage: ReaderPage.ChapterPage

/*    init {
        addView(imageLoadProgress)
        loadJob = MainScope().launch {
            loadPage()
        }
    }*/

    fun bind(readerPage: ReaderPage.ChapterPage){
        this.readerPage = readerPage
        if (readerPage.state != PageState.READY) {
            addView(imageLoadProgress)
        }
        loadJob = MainScope().launch {
            loadPage()
        }
    }

    private suspend fun loadPage() {
        supervisorScope {
            launch(Dispatchers.IO) {
                readerPage.loadPage() // 加载stream
            }
            launch {
                /*                page.streamGetter?.invoke(page.index)?.let {
                                    setupPageContent(it)
                                }*/
                readerPage.stateFlow.collectLatest {
                    when (it) {
                        PageState.WAIT -> {
                            imageLoadProgress.show()
                        }

                        PageState.LOAD_URL -> {
                            imageLoadProgress.show()

                        }

                        PageState.LOAD_PAGE -> {
                            imageLoadProgress.show()

                        }

                        PageState.DOWNLOAD_IMAGE -> {

                        }

                        PageState.READY -> {
                            readerPage.streamGetter?.invoke()?.let { stream ->
                                setupPageContent(stream)
                            }
                            imageLoadProgress.hide()
                        }

                        PageState.ERROR -> {

                        }
                    }
                }
            }
        }
    }

    override fun bind(readerPage: ReaderPage) {
        if (readerPage is ReaderPage.ChapterPage) {
            bind(readerPage)
        }
    }

}