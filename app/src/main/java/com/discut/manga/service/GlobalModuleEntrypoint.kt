package com.discut.manga.service

import com.discut.manga.service.cache.ImageCache
import com.discut.manga.service.cache.PagesCache
import com.discut.manga.service.saver.download.DownloadProvider
import com.discut.manga.service.source.SourceManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GlobalModuleEntrypoint {
    fun getSourceManagerInstance(): SourceManager

    fun getImageCacheInstance(): ImageCache

    fun getPagesCacheInstance(): PagesCache

    fun getDownloadProviderInstance(): DownloadProvider

}