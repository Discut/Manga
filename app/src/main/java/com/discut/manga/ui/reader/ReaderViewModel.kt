package com.discut.manga.ui.reader

import androidx.lifecycle.viewModelScope
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.App
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.data.shouldRead
import com.discut.manga.domain.history.MangaChapterHistory
import com.discut.manga.service.history.IHistoryProvider
import com.discut.manga.service.source.ISourceManager
import com.discut.manga.ui.reader.domain.CurrentChapters
import com.discut.manga.ui.reader.domain.ReaderActivityEffect
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.domain.ReaderActivityState
import com.discut.manga.ui.reader.viewer.domain.ReaderChapter
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import com.discut.manga.ui.reader.viewer.loader.ChapterLoader
import com.discut.manga.ui.reader.viewer.loader.IChapterLoader
import com.discut.manga.util.launchIO
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.chapter.Chapter
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val sourceManager: ISourceManager,
    private val historyProvider: IHistoryProvider
) :
    BaseViewModel<ReaderActivityState, ReaderActivityEvent, ReaderActivityEffect>() {

    private val dbManager = MangaAppDatabase.DB

    private val scope = viewModelScope
    override fun initialState(): ReaderActivityState = ReaderActivityState(
    )

    override suspend fun handleEvent(
        event: ReaderActivityEvent,
        state: ReaderActivityState
    ): ReaderActivityState {
        return when (event) {
            is ReaderActivityEvent.Initialize -> {
                withContext(NonCancellable) {
                    val initState = init(event.mangaId, event.chapterId)
                    val pair = initState.getOrDefault(Pair(false, state))
                    if (pair.first.not()) {
                        val exception =
                            initState.exceptionOrNull() ?: IllegalStateException("Unknown error")
                        sendEffect(ReaderActivityEffect.InitChapterError(exception))
                    }
                    pair.second.currentChapters?.apply {
                        withIOContext {
                            historyProvider.insert(
                                MangaChapterHistory(
                                    historyId = SnowFlakeUtil.generateSnowFlake(),
                                    mangaId = event.mangaId,
                                    chapterId = event.chapterId,
                                    mangaTitle = pair.second.manga?.title ?: "",
                                    chapterName = currReaderChapter.dbChapter.name,
                                    thumbnailUrl = pair.second.manga?.thumbnailUrl,
                                    readAt = System.currentTimeMillis(),
                                )
                            )
                        }
                    }

                    pair.second
                }
            }

            is ReaderActivityEvent.ReaderNavigationMenuVisibleChange -> {
                state.copy(isMenuShow = event.visible)
            }

            is ReaderActivityEvent.PageSelected -> {
                scope.launchIO {
                    if (state.manga == null) {
                        return@launchIO
                    }
                    state.currentChapters?.currReaderChapter?.let { rc ->
                        when (val chapterState = rc.state) {
                            is ReaderChapter.State.Loaded -> {
                                val count =
                                    chapterState.pages.filterIsInstance<ReaderPage.ChapterPage>()
                                        .count()
                                rc.dbChapter.apply {
                                    dbManager.chapterDao().update(
                                        this.copy(
                                            lastPageRead = event.index.toLong(),
                                            read = shouldRead(),
                                            pagesCount = count.toLong()
                                        )
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
                state.copy(currentPage = event.index)
            }

        }
    }

    private suspend fun init(
        mangaId: Long,
        chapterId: Long
    ): Result<Pair<Boolean, ReaderActivityState>> {
        return withIOContext {
            try {
                val manga = dbManager.mangaDao().getById(mangaId)
                    ?: return@withIOContext Result.success(Pair(false, uiState.value))
                val chapters = dbManager.chapterDao().getAllInManga(mangaId)
                if (chapters.isEmpty()) {
                    return@withIOContext Result.success(Pair(false, uiState.value))
                }
                val chaptersForReader = initChaptersForReader(chapters)
                val readerChapter = chaptersForReader.find { it.dbChapter.id == chapterId }
                    ?: return@withIOContext Result.success(Pair(false, uiState.value))
                val source = sourceManager.get(manga.source)
                    ?: return@withIOContext Result.success(Pair(false, uiState.value))
                val chapterLoader = ChapterLoader(App.instance, manga, source)
                val currentChapters =
                    buildViewerChapters(chapterLoader, chaptersForReader, readerChapter)
                val newValue = uiState.value.copy(
                    manga = manga,
                    readerChapters = chaptersForReader,
                    currentChapters = currentChapters
                )
                return@withIOContext Result.success(Pair(true, newValue))
                // 参考eu.kanade.tachiyomi.ui.reader.ReaderActivity#onCreate 中的 viewModel.init(manga, chapter)
                // 与 eu.kanade.tachiyomi.ui.reader.ReaderViewModel#init
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }

    private fun initChaptersForReader(chapters: List<Chapter>): List<ReaderChapter> {
        val readerChapters = chapters.map {
            ReaderChapter(it)
        }
        return readerChapters
    }

    private suspend fun buildViewerChapters(
        loader: IChapterLoader,
        chapters: List<ReaderChapter>,
        chapter: ReaderChapter
    ): CurrentChapters {
        loader.loadChapter(chapter)

        val index = chapters.indexOf(chapter)

        return CurrentChapters(
            chapters.getOrNull(index - 1),
            chapter,
            chapters.getOrNull(index + 1)
        )
    }
}