package com.discut.manga.components.domain

import com.discut.manga.ui.manga.details.MangaDetails
import discut.manga.data.manga.Manga

data class MangaCoverInfo(
    val coverUrl: String? = null,
    val title: String,
    val contentDescription: String = ""
)

internal fun Manga.toMangaCoverInfo() = MangaCoverInfo(
    coverUrl = thumbnailUrl,
    title = title
)

internal fun MangaDetails.toMangaCoverInfo() = MangaCoverInfo(
    coverUrl = coverUrl,
    title = title
)