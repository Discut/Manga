package com.discut.manga.service.manga

import androidx.paging.PagingSource
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.Flow
import managa.source.domain.FilterList
import managa.source.domain.SManga
import java.time.ZonedDateTime


typealias FetchWindow = Pair<Long, Long>

interface IMangaProvider {
    fun search(sourceId: Long, query: String, filterList: FilterList): PagingSource<Long, SManga>

    fun getPopular(sourceId: Long): PagingSource<Long, SManga>

    fun getLatest(sourceId: Long): PagingSource<Long, SManga>

    fun subscribe(mangaId: Long): Flow<Manga>

    suspend fun updateFetchInterval(
        manga: Manga,
        dateTime: ZonedDateTime? = null,
        window: FetchWindow? = null,
    ): Boolean
}