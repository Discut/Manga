package com.discut.manga.service.saver.download.model

fun Downloader.canDownload(): Boolean {
    return status != Downloader.DownloadState.Downloaded
}