package com.discut.manga.ui.manga.details

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.data.extensions.sortedByChapterNumber
import com.discut.manga.service.chapter.ChapterSaver
import com.discut.manga.service.chapter.IChapterProvider
import com.discut.manga.util.launchIO
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.chapter.Chapter
import discut.manga.data.manga.Manga
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val db: MangaAppDatabase,
    private val chapterProvider: IChapterProvider,
    private val chapterSaver: ChapterSaver
) : BaseViewModel<MangaDetailsState, MangaDetailsEvent, MangaDetailsEffect>() {
    override fun initialState(): MangaDetailsState = MangaDetailsState()

    override suspend fun handleEvent(
        event: MangaDetailsEvent,
        state: MangaDetailsState
    ): MangaDetailsState? {
        return when (event) {
            is MangaDetailsEvent.Init -> {
                return try {
                    val manga = getManga(event.mangaId)
                    launchIO {
                        collectChapters(manga.id)
                    }
                    launchIO {
                        chapterSaver.update(manga.source, manga.id)
                    }
                    state.copy(
                        loadState = MangaDetailsState.LoadState.Loaded(manga.toMangaDetails()),
                        manga = manga,
                    )
                } catch (e: Exception) {
                    state.copy(loadState = MangaDetailsState.LoadState.Error(e))
                }
            }

            is MangaDetailsEvent.BootSync -> {
                state.manga?.let {
                    asyncFetchMangaAndChapters(it.id)
                    state.copy(
                        isLoading = true
                    )
                }
            }

            is MangaDetailsEvent.Synced -> {
                state.copy(
                    isLoading = false
                )
            }

            is MangaDetailsEvent.ReadChapter -> {
                launchIO {
                    db.chapterDao().update(
                        event.chapter.copy(
                            read = true,
                            lastPageRead = event.chapter.pagesCount
                        )
                    )
                }
                state
            }

            is MangaDetailsEvent.UnreadChapter -> {
                launchIO {
                    db.chapterDao().update(
                        event.chapter.copy(
                            read = false,
                            lastPageRead = 0
                        )
                    )
                }
                state
            }

            is MangaDetailsEvent.FavoriteManga -> {
                val manga = event.manga.copy(
                    favorite = !event.manga.favorite
                )
                launchIO {
                    db.mangaDao().update(manga)
                }
                state.copy(
                    loadState = MangaDetailsState.LoadState.Loaded(manga.toMangaDetails()),
                    manga = manga
                )
            }

            is MangaDetailsEvent.ChaptersUpdated -> {
                state.copy(
                    chapters = event.chapters.sortedByChapterNumber().asReversed()
                )
            }
        }
    }

    private suspend fun collectChapters(mangaId: Long) {
        chapterProvider.subscribe(mangaId).collect {
            sendEvent(MangaDetailsEvent.ChaptersUpdated(it))
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

    fun collectionChapterInfo(chapter: Chapter): Flow<Chapter> {
        return db.chapterDao().getByIdAsFlow(chapter.id)
    }

    private fun asyncFetchMangaAndChapters(mangaId: Long) {
        launchIO {
            val manga = getManga(mangaId)
            val fetchTask = listOf(
                async { chapterSaver.update(manga.source, manga.id) },
            )
            fetchTask.awaitAll()
            sendEvent(MangaDetailsEvent.Synced)
        }
    }

    class InitMangaDetailsException(msg: String) : Exception(msg)
}