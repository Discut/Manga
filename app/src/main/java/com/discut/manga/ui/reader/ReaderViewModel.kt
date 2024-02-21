package com.discut.manga.ui.reader

import androidx.lifecycle.viewModelScope
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.App
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.data.shouldRead
import com.discut.manga.domain.history.MangaChapterHistory
import com.discut.manga.preference.ReaderPreference
import com.discut.manga.service.history.IHistoryProvider
import com.discut.manga.service.source.ISourceManager
import com.discut.manga.ui.reader.domain.CurrentChapters
import com.discut.manga.ui.reader.domain.ReaderActivityEffect
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.domain.ReaderActivityState
import com.discut.manga.ui.reader.domain.ReaderChapter
import com.discut.manga.ui.reader.domain.ReaderPage
import com.discut.manga.ui.reader.loader.ChapterLoader
import com.discut.manga.ui.reader.loader.IChapterLoader
import com.discut.manga.ui.reader.utils.getNextOrNull
import com.discut.manga.ui.reader.utils.getPrevOrNull
import com.discut.manga.ui.util.isNull
import com.discut.manga.util.get
import com.discut.manga.util.launchIO
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.chapter.Chapter
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import manga.core.preference.PreferenceManager
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val sourceManager: ISourceManager,
    private val historyProvider: IHistoryProvider
) :
    BaseViewModel<ReaderActivityState, ReaderActivityEvent, ReaderActivityEffect>() {

    private val dbManager = MangaAppDatabase.DB
    private val pref = PreferenceManager.get<ReaderPreference>()


    private val scope = viewModelScope
    override fun initialState(): ReaderActivityState = ReaderActivityState(
        readerMode = pref.readerMode
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
                    /*pair.second.currentChapters?.apply {
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
                    }*/

                    updateHistory(pair.second)

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

            is ReaderActivityEvent.ReaderModeChange -> {
                pref.readerMode = event.mode
                state.copy(
                    readerMode = event.mode,
                    currentChapters = state.currentChapters?.copy(
                        prevReaderChapter = state.currentChapters.prevReaderChapter
                    )
                )
            }

            is ReaderActivityEvent.SwitchToChapter -> {
                val manga = state.manga ?: return state
                val source = sourceManager.get(state.manga.source) ?: return state
                val currentChapters = withIOContext {
                    val chapterLoader = ChapterLoader(App.instance, manga, source)
                    buildViewerChapters(chapterLoader, state.readerChapters, event.chapter)
                }
                state.copy(
                    currentChapters = currentChapters
                ).apply {
                    updateHistory(this)
                }
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

    private suspend fun updateHistory(state: ReaderActivityState) =
        withIOContext {
            if (state.currentChapters.isNull()) {
                return@withIOContext
            }
            if (state.manga.isNull()) {
                return@withIOContext
            }

            historyProvider.insert(
                MangaChapterHistory(
                    historyId = SnowFlakeUtil.generateSnowFlake(),
                    mangaId = state.manga!!.id,
                    chapterId = state.currentChapters!!.currReaderChapter.dbChapter.id,
                    mangaTitle = state.manga.title,
                    chapterName = state.currentChapters.currReaderChapter.dbChapter.name,
                    thumbnailUrl = state.manga.thumbnailUrl,
                    readAt = System.currentTimeMillis(),
                )
            )
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
        val index = chapters.indexOf(chapter)

        loader.loadChapter(chapter)
        val prev = chapters.getPrevOrNull(index)?.apply {
            loader.loadChapter(this)
        }
        val next = chapters.getNextOrNull(index)?.apply {
            loader.loadChapter(this)
        }


        return CurrentChapters(
            prev,
            chapter,
            next
        )
    }
}