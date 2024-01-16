package com.discut.manga.service.chapter

import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.data.shouldUpdate
import com.discut.manga.data.toChapter
import com.discut.manga.data.manga.toSManga
import com.discut.manga.service.manga.FetchWindow
import com.discut.manga.service.manga.IMangaProvider
import com.discut.manga.service.source.ISourceManager
import discut.manga.data.MangaAppDatabase
import discut.manga.data.chapter.Chapter
import discut.manga.data.chapter.ChapterDao
import discut.manga.data.manga.MangaDao
import kotlinx.coroutines.flow.first
import manga.source.domain.utils.ChapterRecognition
import java.time.ZonedDateTime
import java.util.Date
import java.util.TreeSet
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class ChapterSaver @Inject constructor(
    private val sourceManager: ISourceManager,
    private val mangaProvider: IMangaProvider
) {

    private val mangaDb: MangaDao = MangaAppDatabase.DB.mangaDao()
    private val chapterDb: ChapterDao = MangaAppDatabase.DB.chapterDao()
    suspend fun update(
        mangaId: Long,
        sourceId: Long,
        manualFetch: Boolean = false,
        fetchWindow: FetchWindow = Pair(0, 0),
    ): List<Chapter> {
        val manga = mangaDb.getById(mangaId) ?: throw Exception("Manga not found")
        val source = sourceManager.get(sourceId) ?: throw Exception("Source not found")
        val sChapters = source.fetchChapterList(manga.toSManga()).first()
            .distinctBy { it.url }
            .mapIndexed { index, sChapter ->
                sChapter.toChapter().copy(
                    mangaId = mangaId,
                    sourceOrder = index.toLong(),
                )
            }
        // Used to not set upload date of older chapters
        // to a higher value than newer chapters
        var maxSeenUploadDate = 0L

        val rightNow = Date().time
        val now = ZonedDateTime.now()

        // Chapters from db.
        val dbChapters = chapterDb.getAllInManga(mangaId)

        // Chapters from the source not in db.
        val toAdd = mutableListOf<Chapter>()

        // Chapters whose metadata have changed.
        val toChange = mutableListOf<Chapter>()

        // Chapters from the db not in source.
        val toDelete = dbChapters.filterNot { dbChapter ->
            sChapters.any { sourceChapter ->
                dbChapter.url == sourceChapter.url
            }
        }

        for (sChapter in sChapters) {
            var chapter = sChapter


            // Recognize chapter number for the chapter.
            val chapterNumber = ChapterRecognition.parseChapterNumber(
                manga.title,
                chapter.name,
                chapter.chapterNumber
            )
            chapter = chapter.copy(chapterNumber = chapterNumber)


            when (val dbChapter = dbChapters.find { it.url == chapter.url }) {
                null -> {// New chapter
                    val toAddChapter = if (chapter.dateUpload == 0L) {
                        val altDateUpload =
                            if (maxSeenUploadDate == 0L) rightNow else maxSeenUploadDate
                        chapter.copy(dateUpload = altDateUpload)
                    } else {
                        maxSeenUploadDate =
                            max(maxSeenUploadDate, sChapter.dateUpload)
                        chapter
                    }
                    toAdd.add(toAddChapter)
                }

                else -> {// Chapter already in db
                    if (!chapter.shouldUpdate(dbChapter)) {
                        continue
                    }
                    /*                    val shouldRenameChapter = downloadProvider.isChapterDirNameChanged(dbChapter, chapter) &&
                                                downloadManager.isChapterDownloaded(
                                                    dbChapter.name, dbChapter.scanlator, manga.title, manga.source,
                                                )

                                        if (shouldRenameChapter) {
                                            downloadManager.renameChapter(source, manga, dbChapter, chapter)
                                        }*/
                    var toChangeChapter = dbChapter.copy(
                        name = chapter.name,
                        chapterNumber = chapter.chapterNumber,
                        scanlator = chapter.scanlator,
                        sourceOrder = chapter.sourceOrder,
                    )
                    if (chapter.dateUpload != 0L) {
                        toChangeChapter = toChangeChapter.copy(dateUpload = chapter.dateUpload)
                    }
                    toChange.add(toChangeChapter)
                }
            }
        }

        // Return if there's nothing to add, delete or change, avoiding unnecessary db transactions.
        if (toAdd.isEmpty() && toDelete.isEmpty() && toChange.isEmpty()) {
            if (manualFetch || manga.fetchInterval == 0 || manga.nextUpdate < fetchWindow.first) {
                mangaProvider.updateFetchInterval(
                    manga,
                    now,
                    fetchWindow
                )
            }
            return emptyList()
        }
        val reAdded = mutableListOf<Chapter>()

        val deletedChapterNumbers = TreeSet<Double>()
        val deletedReadChapterNumbers = TreeSet<Double>()
        val deletedBookmarkedChapterNumbers = TreeSet<Double>()

        toDelete.forEach { chapter ->
            if (chapter.read) deletedReadChapterNumbers.add(chapter.chapterNumber)
            if (chapter.bookmark) deletedBookmarkedChapterNumbers.add(chapter.chapterNumber)
            deletedChapterNumbers.add(chapter.chapterNumber)
        }

        val deletedChapterNumberDateFetchMap = toDelete.sortedByDescending { it.dateFetch }
            .associate { it.chapterNumber to it.dateFetch }
        // Date fetch is set in such a way that the upper ones will have bigger value than the lower ones
        // Sources MUST return the chapters from most to less recent, which is common.
        var itemCount = toAdd.size
        var updatedToAdd = toAdd.map { toAddItem ->
            var chapter = toAddItem.copy(dateFetch = rightNow + itemCount--)

            if (chapter.isRecognizedNumber.not() || chapter.chapterNumber !in deletedChapterNumbers) return@map chapter

            chapter = chapter.copy(
                read = chapter.chapterNumber in deletedReadChapterNumbers,
                bookmark = chapter.chapterNumber in deletedBookmarkedChapterNumbers,
            )

            // Try to to use the fetch date of the original entry to not pollute 'Updates' tab
            deletedChapterNumberDateFetchMap[chapter.chapterNumber]?.let {
                chapter = chapter.copy(dateFetch = it)
            }

            reAdded.add(chapter)

            chapter
        }

        if (toDelete.isNotEmpty()) {
            toDelete.map(chapterDb::delete)
        }

        if (updatedToAdd.isNotEmpty()) {
            updatedToAdd.map {
                it.copy(
                    id = SnowFlakeUtil.generateSnowFlake()
                )
            }.map(chapterDb::insert)
            //updatedToAdd = chapterRepository.addAll(updatedToAdd)
        }

        if (toChange.isNotEmpty()) {
            /*val chapterUpdates = toChange.map { it.toChapterUpdate() }
            updateChapter.awaitAll(chapterUpdates)*/
            toChange.map(chapterDb::update)
        }
        //updateManga.awaitUpdateFetchInterval(manga, now, fetchWindow)
        mangaProvider.updateFetchInterval(
            manga,
            now,
            fetchWindow
        )

        // Set this manga as updated since chapters were changed
        // Note that last_update actually represents last time the chapter list changed at all
        //updateManga.awaitUpdateLastUpdate(manga.id)
        mangaProvider.update(manga.id){
            lastUpdate = Date().time
        }
/*        mangaDb.update(
            manga.copy(
                lastUpdate = Date().time,
            )
        )*/

        val reAddedUrls = reAdded.map { it.url }.toHashSet()

        // val excludedScanlators = getExcludedScanlators.await(manga.id).toHashSet()

        return updatedToAdd.filterNot {
            it.url in reAddedUrls //|| it.scanlator in excludedScanlators
        }

    }
}