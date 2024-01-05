package com.discut.manga.ui.reader.viewer.loader

import com.discut.manga.data.generateHashKey
import com.discut.manga.data.toSChapter
import com.discut.manga.service.cache.ImageCache
import com.discut.manga.service.cache.instance
import com.discut.manga.ui.reader.viewer.domain.PageState
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import discut.manga.data.chapter.Chapter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import managa.source.HttpSource
import managa.source.extensions.toInputStream

class HttpPageLoader(
    private val chapter: Chapter,
    private val source: HttpSource,
    private val imageCache: ImageCache = ImageCache.instance
) : IPageLoader {
    override suspend fun buildPages(): List<ReaderPage> {
        return source.getPageList(chapter.toSChapter()).map {
            ReaderPage.ChapterPage(index = it.index, url = source.baseUrl + chapter.url).apply {
                state = PageState.WAIT
                loadUrl = {
                    state = PageState.LOAD_URL
                    it.imageUrl = if (it.imageUrl.isNullOrEmpty()) {
                        state = PageState.LOAD_PAGE
                        source.getImageUrl(page = it)
                    } else {
                        it.imageUrl
                    }
                    imageUrl = it.imageUrl
                }
                loadPage = {
                    state = PageState.LOAD_PAGE
                    coroutineScope {
                        if (!imageCache.isExist(it.imageUrl!!.generateHashKey())) {
                            state = PageState.DOWNLOAD_IMAGE

                            val job = async {
                                it.progressFlow.collect { p ->
                                    progress = p
                                }
                            }
                            val image = source.getImage(page = it)
                            imageCache.put(it.imageUrl!!.generateHashKey(), image.toInputStream())
                            job.cancel()
                        }
                    }
                    streamGetter = {
                        imageCache.getAsStream(it.imageUrl!!.generateHashKey())
                    }
                    state = PageState.READY
                }
            }
        }
    }

    override suspend fun loadPage(readerPage: ReaderPage) {
        TODO("Not yet implemented")
    }
}