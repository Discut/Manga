package com.discut.manga.ui.reader

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.App
import com.discut.manga.source.ISourceManager
import com.discut.manga.ui.reader.domain.CurrentChapters
import com.discut.manga.ui.reader.domain.ReaderActivityEffect
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.domain.ReaderActivityState
import com.discut.manga.ui.reader.viewer.domain.ReaderChapter
import com.discut.manga.ui.reader.viewer.loader.ChapterLoader
import com.discut.manga.ui.reader.viewer.loader.IChapterLoader
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
) :
    BaseViewModel<ReaderActivityState, ReaderActivityEvent, ReaderActivityEffect>() {

    private val dbManager = MangaAppDatabase.DB
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
                    if (initState.getOrDefault(false).not()) {
                        val exception =
                            initState.exceptionOrNull() ?: IllegalStateException("Unknown error")
                        sendEffect(ReaderActivityEffect.InitChapterError(exception))
                    }
                }
                state
            }
        }
    }

    private suspend fun init(mangaId: Long, chapterId: Long): Result<Boolean> {
        return withIOContext {
            try {
                val manga = dbManager.mangaDao().getById(mangaId)
                    ?: return@withIOContext Result.success(false)
                val chapters = dbManager.chapterDao().getAllInManga(mangaId)
                if (chapters.isEmpty()) {
                    return@withIOContext Result.success(false)
                }
                val chaptersForReader = initChaptersForReader(chapters)
                val readerChapter = chaptersForReader.find { it.dbChapter.id == chapterId }
                    ?: return@withIOContext Result.success(false)
                val source = sourceManager.get(manga.source)
                    ?: return@withIOContext Result.success(false)
                val chapterLoader = ChapterLoader(App.instance, manga, source)
                val currentChapters =
                    buildViewerChapters(chapterLoader, chaptersForReader, readerChapter)


                updateState {
                    copy(
                        manga = manga,
                        readerChapters = chaptersForReader,
                        currentChapters = currentChapters
                    )
                }
                return@withIOContext Result.success(true)
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