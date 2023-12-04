package com.discut.manga.ui.manga.details

import com.discut.manga.data.extensions.getSource
import com.discut.manga.getResourceString
import discut.manga.common.res.R
import discut.manga.data.manga.Manga

data class MangaDetails(
    val title: String,
    val url: String,
    val coverUrl: String,
    val author: String,
    val artist: String,
    val description: String,
    val source: String
)

internal fun Manga.toMangaDetails(): MangaDetails {
    return MangaDetails(
        title = title.ifBlank { getResourceString(R.string.unknown_manga_title) },
        description = description ?: "",
        url = url,
        coverUrl = thumbnailUrl ?: "",
        author = author ?: "",
        artist = artist ?: "",
        source = getSource()?.name ?: getResourceString(R.string.unknown_source),
    )
}
