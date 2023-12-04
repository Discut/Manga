package com.discut.manga.ui.manga.details

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.source.ISourceManager
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.manga.Manga
import kotlinx.coroutines.delay
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
                    val manga = initManga(event.mangaId)
                    state.copy(
                        loadState = MangaDetailsState.LoadState.Loaded(manga.toMangaDetails()),
                        manga = manga
                    )
                } catch (e: Exception) {
                    state.copy(loadState = MangaDetailsState.LoadState.Error(e))
                }
            }
        }
    }

    private suspend fun initManga(mangaId: Long): Manga {
        return withIOContext {
            db.mangaDao().getById(mangaId)
                ?: throw InitMangaDetailsException("Could not find manga with id $mangaId")
        }
    }

    class InitMangaDetailsException(msg: String) : Exception(msg)
}