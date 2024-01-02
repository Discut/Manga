package com.discut.manga.ui.source.viewer

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.discut.domain.ListDisplayLayout
import com.discut.manga.navigation.NavigationEvent
import com.discut.manga.ui.common.LoadingScreen
import com.discut.manga.ui.source.viewer.component.LooseGridLayout
import com.discut.manga.util.postBy
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun MangaViewerContent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    displayLayout: ListDisplayLayout = ListDisplayLayout.LooseGrid,
    mangaList: LazyPagingItems<StateFlow<Manga>>
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val errorState = mangaList.loadState.refresh.takeIf { it is LoadState.Error }
        ?: mangaList.loadState.append.takeIf { it is LoadState.Error }

    /*    val getErrorMessage: (LoadState.Error) -> String = { state ->
            with(context) { state.error.formattedMessage }
        }*/

    LaunchedEffect(errorState) {
        if (mangaList.itemCount > 0 && errorState != null && errorState is LoadState.Error) {
            val result = snackbarHostState.showSnackbar(
                message = /*getErrorMessage(errorState)*/"Error: ${errorState.error}",
                actionLabel = "retry",
                duration = SnackbarDuration.Indefinite,
            )
            when (result) {
                SnackbarResult.Dismissed -> snackbarHostState.currentSnackbarData?.dismiss()
                SnackbarResult.ActionPerformed -> mangaList.retry()
            }
        }
    }

    if (mangaList.itemCount == 0 && mangaList.loadState.refresh is LoadState.Loading) {
        LoadingScreen(
            modifier = Modifier.then(modifier),
        )
        return
    }


    when (displayLayout) {
        ListDisplayLayout.List -> TODO()
        ListDisplayLayout.LooseGrid -> {
            LooseGridLayout(modifier = modifier, mangaList = mangaList, onBookClick = {
                NavigationEvent("mangaDetails/${it.id}").postBy(scope)
            })
        }
    }
}