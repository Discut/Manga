package com.discut.manga.ui.manga.details

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.source.ISourceManager
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.chapter.Chapter
import discut.manga.data.manga.Manga
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val db: MangaAppDatabase,
    private val sourceManager: ISourceManager
) : BaseViewModel<MangaDetailsState, MangaDetailsEvent, MangaDetailsEffect>() {
    override fun initialState(): MangaDetailsState = MangaDetailsState()

    override suspend fun handleEvent(
        event: MangaDetailsEvent,
        state: MangaDetailsState
    ): MangaDetailsState? {
        when (event) {
            is MangaDetailsEvent.Init -> {
                return try {
                    val manga = getManga(event.mangaId)
                    val chapters = getChapter(event.mangaId)
                    state.copy(
                        loadState = MangaDetailsState.LoadState.Loaded(manga.toMangaDetails()),
                        manga = manga,
                        chapters = chapters
                    )
                } catch (e: Exception) {
                    state.copy(loadState = MangaDetailsState.LoadState.Error(e))
                }
            }
        }
    }

    private suspend fun getManga(mangaId: Long): Manga {
        return withIOContext {
            db.mangaDao().getById(mangaId)
                ?: throw InitMangaDetailsException("Could not find manga with id $mangaId")
        }
    }

    private suspend fun getChapter(mangaId: Long): List<Chapter> {
        return withIOContext {
            db.chapterDao().getAllInManga(mangaId)
        }
    }

    class InitMangaDetailsException(msg: String) : Exception(msg)
}