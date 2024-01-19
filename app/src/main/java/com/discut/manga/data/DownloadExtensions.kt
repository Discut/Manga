package com.discut.manga.data

import com.discut.manga.service.saver.download.model.Downloader
import discut.manga.data.download.Download
import discut.manga.data.download.DownloadState.Completed
import discut.manga.data.download.DownloadState.Downloading
import discut.manga.data.download.DownloadState.Error
import discut.manga.data.download.DownloadState.InQueue
import discut.manga.data.download.DownloadState.NotInQueue
import discut.manga.data.download.DownloadState.Waiting

fun Download.transitionToDownloaderState() =
    when (status) {
        NotInQueue -> Downloader.DownloadState.Error(msg = remarks)
        Waiting -> Downloader.DownloadState.Waiting
        Downloading -> Downloader.DownloadState.Downloading
        Completed -> Downloader.DownloadState.Downloaded
        InQueue -> Downloader.DownloadState.InQueue
        Error -> Downloader.DownloadState.Error(msg = remarks)
    }