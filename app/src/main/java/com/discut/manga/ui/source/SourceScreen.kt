package com.discut.manga.ui.source

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.discut.manga.App
import com.discut.manga.util.withIOContext
import discut.manga.source.local.LocalSource
import kotlinx.coroutines.flow.single

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SourceScreen() {
    /*Scaffold(

    ) {

    }*/

    val localSource = LocalSource(App.instance)
    LaunchedEffect(key1 = localSource ){
        withIOContext {
            val popularManga = localSource.getPopularManga(0)
            val single = localSource.fetchChapterList(popularManga.mangas[0]).single()

        }
    }

}