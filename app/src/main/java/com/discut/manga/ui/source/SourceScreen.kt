package com.discut.manga.ui.source

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.discut.manga.source.ISourceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SourceScreen(
    sourceManager: ISourceManager,
    vm: SourceViewModel = hiltViewModel()
) {
    val rememberCoroutineScope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Source") },
            windowInsets = WindowInsets.captionBar
        )
    }) {
        SourceScreenImpl(vm = vm, modifier = Modifier.padding(it))
    }

    /*Column {
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
                                        id = SnowFlakeUtil.generateSnowFlake(),
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
    }*/


}