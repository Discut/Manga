package com.discut.manga.data.extensions

import com.discut.manga.App
import com.discut.manga.source.SourceManagerInterface
import dagger.hilt.EntryPoints
import discut.manga.data.manga.Manga
import managa.source.Source

fun Manga.getSource(): Source? {
    val instance = EntryPoints.get(App.instance, SourceManagerInterface::class.java).getInstance()
    return instance.get(source)
}