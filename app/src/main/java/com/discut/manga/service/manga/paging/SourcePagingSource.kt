package com.discut.manga.service.manga.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.discut.manga.util.withIOContext
import manga.source.Source
import manga.source.domain.SManga
import manga.source.domain.SMangas

/**
 * from tachiyomi.data.source
 */
abstract class SourcePagingSource(
    protected val source: Source,
) : PagingSource<Long, SManga>() {

    abstract suspend fun nextPage(currentPage: Int): SMangas

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, SManga> {
        val page = params.key ?: 1
        val size = params.loadSize
        return try {
            val loadMore = withIOContext {
                nextPage(page.toInt())
                    .takeIf { it.mangas.isNotEmpty() }
                    ?: throw NoResultsException()
            }
            val page1 = LoadResult.Page(
                data = loadMore.mangas,
                prevKey = null,
                nextKey = if (loadMore.hasNextPage) page + 1 else null,
            )
            Log.d("SourcePagingSource", "load: $page1")
            page1
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, SManga>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}

class NoResultsException : Exception()
