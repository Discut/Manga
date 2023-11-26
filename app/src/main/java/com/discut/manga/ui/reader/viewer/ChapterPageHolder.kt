package com.discut.manga.ui.reader.viewer

import android.content.Context
import com.discut.manga.components.reader.ReaderProgressIndicatorComponent
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import com.discut.manga.ui.reader.viewer.domain.PageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class ChapterPageHolder(
    context: Context,
    private val readerPage: ReaderPage.ChapterPage,
) : PageImageView(context) {

    private val imageLoadProgress: ReaderProgressIndicatorComponent =
        ReaderProgressIndicatorComponent(context)

    private var loadJob: Job? = null

    init {
        addView(imageLoadProgress)
        loadJob = MainScope().launch {
            loadPage()
        }
    }

    private suspend fun loadPage() {
        supervisorScope {
            launch (Dispatchers.IO){
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

                        PageState.LOAD_PAGE -> {

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

}