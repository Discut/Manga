package com.discut.manga.service.manga

import androidx.paging.PagingSource
import com.discut.manga.service.manga.paging.SourcePopularPagingSource
import com.discut.manga.source.ISourceManager
import discut.manga.data.MangaAppDatabase
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.Flow
import managa.source.domain.FilterList
import managa.source.domain.SManga
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaProviderImpl @Inject constructor(
    private val sourceManager: ISourceManager
) : IMangaProvider {

    private val mangaDb = MangaAppDatabase.DB.mangaDao()
    override fun search(
        sourceId: Long,
        query: String,
        filterList: FilterList
    ): PagingSource<Long, SManga> {
        TODO("Not yet implemented")
    }

    override fun getPopular(sourceId: Long): PagingSource<Long, SManga> {
        val source = sourceManager.get(sourceId) ?: throw Exception("Source not found")
        return SourcePopularPagingSource(source)
    }

    override fun getLatest(sourceId: Long): PagingSource<Long, SManga> {
        TODO("Not yet implemented")
    }

    override fun subscribe(mangaId: Long): Flow<Manga> {
        return mangaDb.getByIdAsFlow(mangaId)
    }

}