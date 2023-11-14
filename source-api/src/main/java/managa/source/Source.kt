package managa.source

import kotlinx.coroutines.flow.Flow
import managa.source.domain.Page
import managa.source.domain.SChapter
import managa.source.domain.SManga

/**
 * Its a basic interface from [Source], extension developer can extend it.
 * it could be online source or local source.
 */
interface Source {
    val id: Long
    val name: String
    val language: String

    fun fetchMangaDetails(manga: SManga): Flow<SManga> =
        throw IllegalStateException("Not used")

    fun fetchChapterList(manga: SManga): Flow<List<SChapter>> =
        throw IllegalStateException("Not used")

    fun fetchPageList(chapter: SChapter): Flow<List<Page>> =
        throw IllegalStateException("Not used")
}