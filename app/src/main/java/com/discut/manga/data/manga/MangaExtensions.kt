package com.discut.manga.data.manga

import com.discut.manga.App
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.service.GlobalModuleEntrypoint
import dagger.hilt.EntryPoints
import discut.manga.data.manga.Manga
import discut.manga.source.local.LocalSource
import managa.source.Source
import managa.source.domain.SManga

fun Manga.getSource(): Source? {
    val instance = EntryPoints.get(App.instance, GlobalModuleEntrypoint::class.java).getSourceManagerInstance()
    return instance.get(source)
}

fun SManga.toManga(fromSource: Long): Manga {
    return Manga.create().copy(
        id = SnowFlakeUtil.generateSnowFlake(),
        title = title,
        url = url,
        source = fromSource,
        thumbnailUrl = thumbnail_url,
        description = description,
        author = author,
        artist = artist,
        genre = genre?.split(",")?.toList() ?: emptyList(),
    )
}

fun Manga.toSManga(): SManga {
    return SManga.create().also {
        it.title = title
        it.url = url
        it.thumbnail_url = thumbnailUrl
        it.description = description
        it.author = author
        it.artist = artist
        it.genre = genre?.joinToString()
        it.initialized = true
    }
}

fun Manga.toUpdateManga():UpdateManga{
    return UpdateManga(id = id)
}

fun Manga.isLocal() = source == LocalSource.ID