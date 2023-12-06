package com.discut.manga.ui.source

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.data.extensions.toManga
import com.discut.manga.source.ISourceManager
import com.discut.manga.util.withIOContext
import discut.manga.data.MangaAppDatabase
import discut.manga.data.chapter.Chapter
import discut.manga.source.local.LocalSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SourceScreen(sourceManager: ISourceManager) {
    /*Scaffold(

    ) {

    }*/

    /*    val localSource = LocalSource(App.instance)
        LaunchedEffect(key1 = localSource ){
            withIOContext {
                val popularManga = localSource.getPopularManga(0)
                val single = localSource.fetchChapterList(popularManga.mangas[0]).single()

            }
        }*/
    val rememberCoroutineScope = rememberCoroutineScope()

    Column {
        TextPreferenceComponent(title = "搜索LocalSource") {
            rememberCoroutineScope.launch {
                val get = sourceManager.get(0)
                when (get) {
                    is LocalSource -> {
                        withIOContext {
                            val popularManga = get.getPopularManga(0)
                            val mangas = popularManga.mangas
                            val map = mangas.map {
                                it.toManga(get.id)
                            }
                            MangaAppDatabase.DB.mangaDao().insert(map.get(0))
                            get.fetchChapterList(mangas[0]).collect {
                                val chapters = it.map {
                                    Chapter.create().copy(
                                        id = SnowFlakeUtil(0, 0).nextId,
                                        mangaId = map.get(0).id,
                                        name = it.name,
                                        url = it.url,
                                        scanlator = it.scanlator,
                                        dateUpload = it.date_upload,
                                        chapterNumber = it.chapter_number.toDouble()
                                    )
                                }
                                chapters.forEach {
                                    MangaAppDatabase.DB.chapterDao().insert(it)
                                }
                            }
                        }

                    }
                }
            }
        }
    }


}