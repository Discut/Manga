package com.discut.manga.ui.bookshelf

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.navigation.NavigationEvent
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.util.postBy
import com.discut.manga.util.withIOContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import discut.manga.data.MangaAppDatabase
import discut.manga.data.manga.Manga
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BookshelfScreen() {
    val context = LocalContext.current
    //val all = MangaAppDatabase.DB.mangaDao().getAll()
    var all by remember {
        mutableStateOf<List<Manga>>(listOf())
    }

    val permissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val rememberPermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
        rememberPermissionState.launchPermissionRequest()
    }

    val rememberCoroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = all) {
        withIOContext {
            all = MangaAppDatabase.DB.mangaDao().getAll()
        }
    }
    Column {
        TextPreferenceComponent(title = "进入Reader") {
            ReaderActivity.startActivity(context, -3618642300592193536L, -3686196295002750976L)
        }
        LazyColumn {
            all.forEach {
                item {
                    TextPreferenceComponent(title = it.title, subTitle = it.url) {
                        NavigationEvent("mangaDetails/${it.id}").postBy(rememberCoroutineScope)
                        /*rememberCoroutineScope.launch {
                            withIOContext {
                                val allInManga =
                                    MangaAppDatabase.DB.chapterDao().getAllInManga(it.id)

                                ReaderActivity.startActivity(
                                    context,
                                    it.id,
                                    allInManga.getOrNull(0)?.id ?: -1
                                )
                            }
                        }*/

                    }
                }
            }
        }
    }

}