package com.discut.manga.ui.manga.details

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.data.sortedByChapterNumber
import com.discut.manga.service.chapter.ChapterSaver
import com.discut.manga.service.chapter.IChapterProvider
import com.discut.manga.service.history.IHistoryProvider
import com.discut.manga.service.manga.IMangaProvider
import com.discut.manga.service.manga.MangaSaver
import com.discut.manga.util.launchIO
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.category.Category
import discut.manga.data.chapter.Chapter
import discut.manga.data.manga.Manga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val db: MangaAppDatabase,
    private val chapterProvider: IChapterProvider,
    private val mangaProvider: IMangaProvider,
    private val historyProvider: IHistoryProvider,
    private val chapterSaver: ChapterSaver,
    private val mangaSaver: MangaSaver
) : BaseViewModel<MangaDetailsState, MangaDetailsEvent, MangaDetailsEffect>() {
    override fun initialState(): MangaDetailsState = MangaDetailsState()

    override suspend fun handleEvent(
        event: MangaDetailsEvent,
        state: MangaDetailsState
    ): MangaDetailsState {
        return when (event) {
            is MangaDetailsEvent.Init -> {
                collectManga(event.mangaId)
                collectChapters(event.mangaId)
                sendEvent(MangaDetailsEvent.BootSync(event.mangaId))
                state.copy(
                    categories = fetchCategories()
                )
            }

            is MangaDetailsEvent.BootSync -> {
                asyncFetchMangaAndChapters(event.mangaId)
                state.copy(
                    isLoading = true
                )
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

            is MangaDetailsEvent.StartToRead -> {
                if (state.manga == null) {
                    return state
                }
                launchIO {
                    when (val history = historyProvider.getLatest(state.manga.id)) {
                        null -> sendEffect(
                            MangaDetailsEffect.JumpToRead(
                                state.manga.id,
                                getFirstChapter().id
                            )
                        )

                        else -> sendEffect(
                            MangaDetailsEffect.JumpToRead(
                                state.manga.id,
                                history.chapterId
                            )
                        )
                    }
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
                launchIO {
                    mangaProvider.update(event.manga.id) {
                        favorite = !event.manga.favorite
                        category = event.manga.category
                    }
                }
                state
            }

            is MangaDetailsEvent.ChaptersUpdated -> {
                state.copy(
                    chapters = event.chapters.sortedByChapterNumber().asReversed()
                )
            }

            is MangaDetailsEvent.MangaUpdated -> {
                state.copy(
                    loadState = MangaDetailsState.LoadState.Loaded(event.manga.toMangaDetails()),
                    manga = event.manga,
                    currentHistory = historyProvider.subscribe(event.manga.id)
                        .distinctUntilChanged().stateIn(
                            CoroutineScope(Dispatchers.IO)
                        )
                )
            }

            is MangaDetailsEvent.AddNewCategory -> {
                val order =
                    state.categories.getOrNull(state.categories.lastIndex)?.order?.plus(1) ?: 0
                val id = SnowFlakeUtil.generateSnowFlake()
                withIOContext {
                    state.copy(
                        categories = db.categoryDao().run {
                            insert(
                                Category(
                                    id = id,
                                    name = event.category,
                                    order = order
                                )
                            )
                            fetchCategories()
                        }
                    )
                }
            }
        }
    }

    private fun getFirstChapter(): Chapter {
        return uiState.value.chapters.last()
    }

    private suspend fun fetchCategories(): List<Category> =
        withIOContext {
            db.categoryDao().getAll().sortedBy { it.order }
        }

    private fun collectChapters(mangaId: Long) {
        launchIO {
            chapterProvider.subscribe(mangaId).collect {
                sendEvent(MangaDetailsEvent.ChaptersUpdated(it))
            }
        }
    }

    private suspend fun collectManga(mangaId: Long) {
        launchIO {
            mangaProvider.subscribe(mangaId).distinctUntilChanged().collect {
                sendEvent(MangaDetailsEvent.MangaUpdated(it))
            }
        }
    }

    private suspend fun getManga(mangaId: Long): Manga {
        return withIOContext {
            db.mangaDao().getById(mangaId)
                ?: throw InitMangaDetailsException("Could not find manga with id $mangaId")
        }
    }

    @Deprecated("Use collectionChapterInfo instead")
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
            try {
                val manga = getManga(mangaId)
                val fetchTask = listOf(
                    async { chapterSaver.update(mangaId = mangaId, sourceId = manga.source) },
                    async { mangaSaver.update(mangaId = mangaId, sourceId = manga.source) }
                )
                fetchTask.awaitAll()
            } finally {
                sendEvent(MangaDetailsEvent.Synced)
            }
        }
    }

    class InitMangaDetailsException(msg: String) : Exception(msg)
}