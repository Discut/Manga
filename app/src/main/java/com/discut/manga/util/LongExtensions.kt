@file:Suppress("unused")
package com.discut.manga.util

import discut.manga.data.MangaAppDatabase
import discut.manga.data.manga.Manga
import discut.manga.data.manga.MangaDao
import java.text.DateFormat
import java.util.Calendar

fun Long?.toMangaFromDB(mangaDao: MangaDao = MangaAppDatabase.DB.mangaDao()): Manga? {
    if (this == null) return null
    return mangaDao.getById(this)
}

fun Long.toDate(dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT)): String {
    return dateFormat.format(this)
}

fun Long.getHourAndMinute(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return "${calendar.get(Calendar.HOUR_OF_DAY)}: ${calendar.get(Calendar.MINUTE)}"
}

fun Long.getYearAndMonthAndDay(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
        calendar.get(
            Calendar.DAY_OF_MONTH
        )
    }"
}