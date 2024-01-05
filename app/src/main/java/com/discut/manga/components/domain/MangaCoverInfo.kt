package com.discut.manga.components.domain

import android.graphics.Color
import com.discut.manga.ui.manga.details.MangaDetails
import discut.manga.data.manga.Manga

data class MangaCoverInfo(
    val coverUrl: String? = null,
    val title: String,
    val contentDescription: String = "",
    val placeholderCoverBackgroundColor: Int = Color.parseColor("#196A71"),
    val placeholderTitle: Pair<String, String> = Pair("N", "O")
)

internal fun Manga.toMangaCoverInfo() = MangaCoverInfo(
    coverUrl = thumbnailUrl,
    title = title,
    placeholderCoverBackgroundColor = placeholderCoverBackgroundColors.random(),
    placeholderTitle = getChars(title)
)

internal fun MangaDetails.toMangaCoverInfo() = MangaCoverInfo(
    coverUrl = coverUrl,
    title = title,
    placeholderCoverBackgroundColor = placeholderCoverBackgroundColors.random(),
    placeholderTitle = getChars(title)
)

private fun getChars(str: String): Pair<String, String> {
    try {
        var chars = str.uppercase()
        if (str.isBlank()) {
            return "N" to "O"
        }
        if (str.length == 1) {
            return str to ""
        }
        chars = chars.replace(" ", "", true)
        val char1 = chars.first().toString()
        chars = chars.replace(char1, "", true)
        val char2 = chars[(chars.indices).random()].toString()
        return char1 to char2
    } catch (e: Exception) {
        return "N" to "O"
    }
}

private val placeholderCoverBackgroundColors = listOf(
    Color.parseColor("#196A71"),
    Color.parseColor("#f9d3e3"),
    Color.parseColor("#6a8d52"),
    Color.parseColor("#f0908d"),
    Color.parseColor("#d9883d"),
)