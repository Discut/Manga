package com.discut.manga.service.manga.paging

import manga.source.Source
import manga.source.domain.SMangas

class SourcePopularPagingSource(source: Source) : SourcePagingSource(source) {
    override suspend fun nextPage(currentPage: Int): SMangas {
        return source.getPopularManga(currentPage)
    }
}