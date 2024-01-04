package com.discut.manga.service.manga

import discut.manga.data.MangaAppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaSaver @Inject constructor() {
    private val db = MangaAppDatabase.DB.mangaDao()

    fun update(){

    }
}