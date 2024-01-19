package com.discut.manga.service.saver.download.model

import com.discut.manga.data.transitionToDownloaderState
import discut.manga.data.chapter.Chapter
import discut.manga.data.download.Download
import discut.manga.data.download.DownloadState
import discut.manga.data.manga.Manga
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import manga.source.HttpSource
import manga.source.domain.Page

data class Downloader(
    val source: HttpSource,
    var download: Download,
    val manga: Manga,
    val chapter: Chapter,
    var pages: List<Page>? = null
) {

    val totalProgress: Int
        get() = pages?.sumOf(Page::progress) ?: 0

    val downloadedImages: Int
        get() = pages?.count { it.status == Page.State.READY } ?: 0

    @Transient
    private val _statusFlow: MutableStateFlow<DownloadState> =
        MutableStateFlow(
            download.transitionToDownloaderState()
        )

    @Transient
    val statusFlow = _statusFlow.asStateFlow()
    var status: DownloadState
        get() = _statusFlow.value
        set(status) {
            _statusFlow.update {
                status
            }
        }

    @OptIn(FlowPreview::class)
    @Transient
    val progressFlow = flow {
        if (pages == null) {
            emit(0)
            while (pages == null) {
                delay(50)
            }
        }

        val progressFlows = pages!!.map(Page::progressFlow)
        emitAll(combine(progressFlows) { it.average().toInt() })
    }
        .distinctUntilChanged()
        .debounce(50)

    val progress: Int
        get() {
            val pages = pages ?: return 0
            return pages.map(Page::progress).average().toInt()
        }


    sealed interface DownloadState {
        data object Downloading : DownloadState
        data object Downloaded : DownloadState
        data class Error(val error: Throwable? = null, val msg: String) : DownloadState
        data object Waiting : DownloadState

        data object InQueue : DownloadState
    }

}

fun Downloader.DownloadState.getNumber() = when (this) {
    Downloader.DownloadState.Downloading -> 0
    Downloader.DownloadState.Waiting -> 1
    Downloader.DownloadState.InQueue -> 2
    is Downloader.DownloadState.Error -> 3
    Downloader.DownloadState.Downloaded -> 2
}