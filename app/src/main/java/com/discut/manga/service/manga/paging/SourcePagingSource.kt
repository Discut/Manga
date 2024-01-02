package com.discut.manga.service.manga.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.discut.manga.util.withIOContext
import managa.source.Source
import managa.source.domain.SManga
import managa.source.domain.SMangas

/**
 * from tachiyomi.data.source
 */
abstract class SourcePagingSource(
    protected val source: Source,
) : PagingSource<Long, SManga>() {

    abstract suspend fun nextPage(currentPage: Int): SMangas

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, SManga> {
        val page = params.key ?: 1

        val mangasPage = try {
            withIOContext {
                nextPage(page.toInt())
                    .takeIf { it.mangas.isNotEmpty() }
                    ?: throw NoResultsException()
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

        return LoadResult.Page(
            data = mangasPage.mangas,
            prevKey = null,
            nextKey = if (mangasPage.hasNextPage) page + 1 else null,
        )
    }

    override fun getRefreshKey(state: PagingState<Long, SManga>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}

class NoResultsException : Exception()
