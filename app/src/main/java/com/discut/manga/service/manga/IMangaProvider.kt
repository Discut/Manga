package com.discut.manga.service.manga

import androidx.paging.PagingSource
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.Flow
import managa.source.domain.FilterList
import managa.source.domain.SManga

interface IMangaProvider {
    fun search(sourceId: Long, query: String, filterList: FilterList): PagingSource<Long, SManga>

    fun getPopular(sourceId: Long): PagingSource<Long, SManga>

    fun getLatest(sourceId: Long): PagingSource<Long, SManga>

    fun subscribe(mangaId: Long): Flow<Manga>
}