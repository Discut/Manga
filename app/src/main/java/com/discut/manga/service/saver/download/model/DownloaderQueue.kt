package com.discut.manga.service.saver.download.model

import kotlin.random.Random

data class DownloaderQueue(
    val version: Int = Random.nextInt(),
    val queue: List<Downloader> = emptyList()
)