package discut.manga.source.local

import kotlinx.coroutines.flow.Flow
import managa.source.Source
import managa.source.domain.Page
import managa.source.domain.SChapter
import managa.source.domain.SManga

class LocalSource : Source {
    override val id: Long
        get() = 0L
    override val name: String
        get() = "LocalSource"
    override val language: String
        get() = "All"

    override fun fetchMangaDetails(manga: SManga): Flow<SManga> {
        return super.fetchMangaDetails(manga)
    }

    override fun fetchChapterList(manga: SManga): Flow<List<SChapter>> {
        return super.fetchChapterList(manga)
    }

    override fun fetchPageList(chapter: SChapter): Flow<List<Page>> {
        return super.fetchPageList(chapter)
    }

}