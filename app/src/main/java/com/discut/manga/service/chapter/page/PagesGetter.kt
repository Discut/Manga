package com.discut.manga.service.chapter.page

import com.discut.manga.data.generateHashKey
import com.discut.manga.data.toSChapter
import com.discut.manga.service.cache.PagesCache
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import discut.manga.data.chapter.Chapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import manga.source.Source
import manga.source.domain.Page

class PagesGetter @AssistedInject constructor(
    @Assisted
    private val source: Source,
    private val pagesCache: PagesCache
) {

    @AssistedFactory
    interface Factory {
        fun create(source: Source): PagesGetter
    }

    suspend fun getPages(chapter: Chapter): List<Page> =
        if (pagesCache.isExist(chapter.generateHashKey())) {
            pagesCache.get(chapter.generateHashKey()).let {
                Json.decodeFromString(it!!)
            }
        } else {
            source.getPageList(chapter.toSChapter()).apply {
                pagesCache.put(chapter.generateHashKey(), Json.encodeToString(this))
            }
        }
}