package com.discut.manga.service.manga

import androidx.paging.PagingSource
import com.discut.manga.data.manga.UpdateManga
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

    fun update(mangaId: Long, builder: UpdateManga.() -> Unit): Int

    fun update(updateManga: UpdateManga): Int

    suspend fun updateFetchInterval(
        manga: Manga,
        dateTime: ZonedDateTime? = null,
        window: FetchWindow? = null,
    ): Boolean
}