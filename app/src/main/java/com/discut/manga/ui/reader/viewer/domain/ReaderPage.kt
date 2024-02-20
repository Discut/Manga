package com.discut.manga.ui.reader.viewer.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream

sealed class ReaderPage(var isLoad: Boolean = false) {

    private val _stateFlow = MutableStateFlow(PageState.WAIT)


    /*    open suspend fun loadPage(){
            _stateFlow.value = PageState.READY
        }*/

    val stateFlow = _stateFlow.asStateFlow()

    var state
        get() = _stateFlow.value
        set(value) {
            _stateFlow.value = value
        }

    class ChapterPage(
        val index: Int,
        val url: String = "",
        var imageUrl: String? = null,
        var streamGetter: (() -> InputStream)? = null,
    ) : ReaderPage() {

        lateinit var loadPage: suspend () -> Unit
        lateinit var loadUrl: suspend () -> Unit

        //private val _stateFlow = MutableStateFlow(PageState.WAIT)

        private val _progressFlow = MutableStateFlow(0)
        var progressFlow = _progressFlow.asStateFlow()

        internal var progress: Int
            get() = _progressFlow.value
            set(value) {
                _progressFlow.value = value
            }

    }

    class ChapterTransition(
        val prevChapter: ReaderChapter?,
        val currChapter: ReaderChapter?,
    ) : ReaderPage(true)


}

enum class PageState {
    WAIT,
    LOAD_URL,
    LOAD_PAGE,
    DOWNLOAD_IMAGE,
    READY,
    ERROR,
}


