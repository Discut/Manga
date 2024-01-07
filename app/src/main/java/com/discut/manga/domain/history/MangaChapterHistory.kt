package com.discut.manga.domain.history

data class MangaChapterHistory(
    val historyId: Long,
    val mangaId: Long,
    val chapterId: Long,
    val mangaTitle: String,
    val chapterName: String,
    val thumbnailUrl: String?,
    val readAt: Long
)