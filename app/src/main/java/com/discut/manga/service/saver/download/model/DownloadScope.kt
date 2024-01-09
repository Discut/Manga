package com.discut.manga.service.saver.download.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import managa.source.HttpSource

class DownloadScope {

    lateinit var source: HttpSource

    private val queue: MutableList<Downloader> = mutableListOf()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    fun add(downloader: Downloader) {
        queue.find { it.download.id == downloader.download.id } ?: queue.add(downloader)
    }

    fun startAll(){

    }

    fun stopAll(){

    }
    fun stop(downloader: Downloader) {

    }


}