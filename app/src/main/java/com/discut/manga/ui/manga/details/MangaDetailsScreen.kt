package com.discut.manga.ui.manga.details

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.components.manga.MangaInfoBox
import com.discut.manga.ui.common.LoadingScreen
import com.discut.manga.ui.manga.details.component.ShortInfoBox
import discut.manga.common.res.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    mangaId: Long,
    vm: MangaDetailsViewModel = hiltViewModel(),

    onBackPressed: () -> Unit,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = mangaId) {
        if (state.loadState is MangaDetailsState.LoadState.Waiting) {
            vm.sendEvent(MangaDetailsEvent.Init(mangaId))
        }
    }
    if (state.loadState is MangaDetailsState.LoadState.Error) {
        return
    }
    if (state.loadState !is MangaDetailsState.LoadState.Loaded) {
        LoadingScreen()
        return
    }

    val loadState = state.loadState as MangaDetailsState.LoadState.Loaded
    val details = loadState.details
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = details.title.ifBlank { stringResource(id = R.string.unknown_manga_title) },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    // modifier = Modifier.alpha(if (isActionMode) 1f else titleAlphaProvider()),
                )
            },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                MangaInfoBox(
                    info = details
                )
            }
            item {
                ShortInfoBox()
            }

        }
    }

}
