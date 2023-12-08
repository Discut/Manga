package com.discut.manga.ui.manga.details

import com.discut.manga.data.extensions.getSource
import com.discut.manga.getResourceString
import discut.manga.common.res.R
import discut.manga.data.manga.Manga

data class MangaDetails(
    val title: String,
    val favorite: Boolean,
    val url: String,
    val coverUrl: String,
    val author: String,
    val artist: String,
    val description: String,
    val source: String,
    val tags: List<String> = emptyList(),
)

internal fun Manga.toMangaDetails(): MangaDetails {
    return MangaDetails(
        title = title.ifBlank { getResourceString(R.string.unknown_manga_title) },
        favorite = favorite,
        description = description ?: "",
        url = url,
        coverUrl = thumbnailUrl ?: "",
        author = author ?: "",
        artist = artist ?: "",
        source = getSource()?.name ?: getResourceString(R.string.unknown_source),
        tags = genre?.cleanTags() ?: emptyList()
    )
}

private fun List<String>.cleanTags() = this.filter { it != "" }
