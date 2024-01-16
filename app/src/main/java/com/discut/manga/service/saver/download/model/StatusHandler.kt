package com.discut.manga.service.saver.download.model

import com.discut.manga.App
import com.discut.manga.service.GlobalModuleEntrypoint
import com.discut.manga.service.saver.download.DownloadProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoints
import discut.manga.data.download.DownloadState

class StatusHandler @AssistedInject constructor(
    @Assisted
    private val downloader: Downloader,
    private val downloadProvider: DownloadProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(downloader: Downloader): StatusHandler
    }

    fun onInQueue() {
        downloader.download = downloader.download.copy(
            status = DownloadState.InQueue
        )
        downloadProvider.updateOrInsertDownload(
            downloader.download
        )
    }

    fun onDownloading() {
        downloader.download = downloader.download.copy(
            status = DownloadState.Downloading
        )
        downloadProvider.updateOrInsertDownload(
            downloader.download
        )
    }

    fun onDownloaded() {
        downloader.download = downloader.download.copy(
            status = DownloadState.Completed
        )
        downloadProvider.updateOrInsertDownload(
            downloader.download
        )
    }

    fun onError() {
        downloader.download = downloader.download.copy(
            status = DownloadState.Error,
            remarks = (downloader.status as? Downloader.DownloadState.Error)?.msg ?: "Error"
        )
        downloadProvider.updateOrInsertDownload(
            downloader.download
        )
    }

    fun onWaiting() {
        downloader.download = downloader.download.copy(
            status = DownloadState.Waiting
        )
        downloadProvider.updateOrInsertDownload(
            downloader.download
        )
    }

    fun onNotInQueue() {
        downloader.download = downloader.download.copy(
            status = DownloadState.NotInQueue
        )
        downloadProvider.updateOrInsertDownload(
            downloader.download
        )
    }
    companion object
}

fun StatusHandler.Companion.create(downloader: Downloader): StatusHandler {
    return EntryPoints.get(App.instance, GlobalModuleEntrypoint::class.java)
        .getDownloadStatusHandlerFactoryInstance().create(downloader)
}