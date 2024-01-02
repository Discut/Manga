package com.discut.manga.service.manga.paging

import managa.source.Source
import managa.source.domain.SMangas

class SourcePopularPagingSource(source: Source) : SourcePagingSource(source) {
    override suspend fun nextPage(currentPage: Int): SMangas {
        return source.getPopularManga(currentPage)
    }
}