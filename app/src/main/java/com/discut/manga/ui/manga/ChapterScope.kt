package com.discut.manga.ui.manga

import discut.manga.data.chapter.Chapter
import discut.manga.data.download.DownloadState
import kotlinx.coroutines.flow.StateFlow

data class ChapterScope(
    val chapter: Chapter,
    val downloadState: StateFlow<DownloadState>
)