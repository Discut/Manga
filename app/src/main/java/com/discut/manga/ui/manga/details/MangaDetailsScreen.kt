package com.discut.manga.ui.manga.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.components.CustomModalBottomSheet
import com.discut.manga.components.SwipeDirection
import com.discut.manga.components.domain.toMangaCoverInfo
import com.discut.manga.components.indicator.AppLinearIndicator
import com.discut.manga.components.manga.MangaCover
import com.discut.manga.components.manga.MangaInfoBox
import com.discut.manga.components.scaffold.AppBarActions
import com.discut.manga.data.shouldRead
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import com.discut.manga.ui.common.LoadingScreen
import com.discut.manga.ui.manga.details.component.AboutBookSheet
import com.discut.manga.ui.manga.details.component.FavoriteButton
import com.discut.manga.ui.manga.details.component.InfoBoxType
import com.discut.manga.ui.manga.details.component.MoreInfoItem
import com.discut.manga.ui.manga.details.component.ShortInfoBox
import com.discut.manga.ui.manga.details.component.SwipeableActionCollection
import com.discut.manga.ui.manga.details.component.SwipeableChapterItem
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.util.isScrolledToEnd
import com.discut.manga.util.isScrollingUp
import com.discut.manga.util.toDate
import discut.manga.common.res.R
import discut.manga.data.chapter.Chapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    mangaId: Long,
    vm: MangaDetailsViewModel = hiltViewModel(),

    onBackPressed: () -> Unit,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showModalBottomSheet by remember {
        mutableStateOf(false)
    }
    /*    var isFavorite by remember {
            mutableStateOf(false)
        }*/
    var isFavoriteOnAppbarAnimationStopped by remember {
        mutableStateOf(true)
    }
    val isShowFavoriteOnAppbar by remember {
        derivedStateOf {
            state.manga?.favorite ?: false && isFavoriteOnAppbarAnimationStopped
        }
    }
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
    val chapterListState = rememberLazyListState()
    val isShowReadFloatButton by remember {
        derivedStateOf { chapterListState.firstVisibleItemIndex > 3 }
    }
    val isShowComplexTitle by remember {
        derivedStateOf { chapterListState.firstVisibleItemIndex > 0 }
    }
    Surface {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    AnimatedVisibility(
                        visible = isShowComplexTitle,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MangaCover.SQUARE(
                                modifier = Modifier.size(28.dp),
                                info = details.toMangaCoverInfo()
                            )
                            Spacer(modifier = Modifier.width(MaterialTheme.padding.Default))
                            Text(
                                text = details.title.ifBlank { stringResource(id = R.string.unknown_manga_title) },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        AnimatedVisibility(visible = isShowFavoriteOnAppbar) {
                            IconButton(onClick = {
                                state.manga?.let {
                                    vm.sendEvent(MangaDetailsEvent.FavoriteManga(it))
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "favorite"
                                )
                            }
                        }
                        /*                        LikeButton(favorite = details.favorite) {
                                                    state.manga?.let {
                                                        vm.sendEvent(MangaDetailsEvent.FavoriteManga(it))
                                                    }
                                                }*/
                        AppBarActions {
                            toOverflowAction {
                                title = "refresh"
                                onClick = {
                                    vm.sendEvent(MangaDetailsEvent.BootSync(mangaId))
                                }
                            }
                        }
                    })
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = isShowReadFloatButton,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    ExtendedFloatingActionButton(
                        text = {
                            Text(
                                text = "Read"
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null
                            )
                        },
                        onClick = {},
                        expanded = chapterListState.isScrollingUp() || chapterListState.isScrolledToEnd(),
                    )
                }
            }
        ) { pv ->
            LaunchedEffect(chapterListState) {
                snapshotFlow { chapterListState.firstVisibleItemIndex }
                    .collect { index ->
                        // 当滑动到了某一项时触发的逻辑
                        println("滑动到了第 $index 项")
                    }
            }
            AppLinearIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(pv), isVisible = state.isLoading
            )
            LazyColumn(modifier = Modifier.padding(pv), state = chapterListState) {
                item {
                    MangaInfoBox(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.padding.Normal),
                        info = details,
                    )
                }
                item {
                    ShortInfoBox(
                        modifier = Modifier.wrapContentHeight(),
                        contexts = listOf(
                            InfoBoxType.Icon("Preview") {
                                Icon(
                                    modifier = it,
                                    imageVector = Icons.Default.Preview,
                                    contentDescription = ""
                                )
                            },
                            InfoBoxType.Title(state.chapters.size.toString(), "章"),
                            InfoBoxType.Icon("Preview") {
                                Icon(
                                    modifier = it,
                                    imageVector = Icons.Default.Preview,
                                    contentDescription = ""
                                )
                            }
                        )
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(
                                horizontal = MaterialTheme.padding.Normal,
                                vertical = MaterialTheme.padding.Large
                            )
                    )
                }
                item {
                    Row(modifier = Modifier.padding(horizontal = MaterialTheme.padding.Normal)) {
                        FilledIconButton(
                            modifier = Modifier.weight(3f),
                            onClick = {
                                isFavoriteOnAppbarAnimationStopped = false
                            }) {
                            Text(text = "开始阅读")
                        }
                        Spacer(modifier = Modifier.width(MaterialTheme.padding.Normal))
                        FavoriteButton(
                            modifier = Modifier.weight(1f),
                            isFavorite = state.manga?.favorite ?: false,
                            onClick = {
                                state.manga?.let {
                                    vm.sendEvent(MangaDetailsEvent.FavoriteManga(it))
                                }
                            },
                            onAnimated = { isFavoriteOnAppbarAnimationStopped = true })
                    }
                }
                item {
                    MoreInfoItem(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.padding.Normal)
                            .padding(bottom = MaterialTheme.padding.Medium),
                        title = "关于此漫画",
                        onClick = {
                            showModalBottomSheet = true
                        }) {
                        Text(
                            text = details.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(MaterialTheme.alpha.Normal),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 3
                        )
                    }
                }
                item {
                    MoreInfoItem(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.padding.Normal),
                        title = "章节",
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.ExpandMore,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        })
                }
                state.chapters.forEachIndexed { index, c ->
                    item {
                        val chapter by vm.collectionChapterInfo(c)
                            .collectAsStateWithLifecycle(initialValue = c)
                        val visibleProgress by remember {
                            derivedStateOf {
                                !chapter.shouldRead() && chapter.lastPageRead > 0
                            }
                        }
                        val alpha by remember {
                            derivedStateOf {
                                if (chapter.read) {
                                    MaterialTheme.alpha.Lowest
                                } else {
                                    MaterialTheme.alpha.Highest
                                }
                            }
                        }
                        SwipeableChapterItem(
                            modifier = Modifier
                                .padding(
                                    horizontal = MaterialTheme.padding.Normal,
                                    vertical = MaterialTheme.padding.Medium
                                )
                                .alpha(alpha),
                            title = chapter.name,
                            subtitle = chapter.getSubtitle(),
                            leftAction = SwipeableActionCollection.Read {},
                            visibleProgress = visibleProgress,

                            onSwipe = {
                                if (it == SwipeDirection.R) {
                                    if (chapter.read) {
                                        vm.sendEvent(MangaDetailsEvent.UnreadChapter(chapter))
                                    } else {
                                        vm.sendEvent(MangaDetailsEvent.ReadChapter(chapter))
                                    }
                                }

                            },
                            progress = {
                                chapter.getReadProgress()
                            },
                            onClick = {
                                ReaderActivity.startActivity(
                                    context,
                                    mangaId,
                                    chapter.id
                                )
                            }
                        )
                    }
                }


            }
        }
        CustomModalBottomSheet(
            isShow = showModalBottomSheet,
            onDismissRequest = {
                showModalBottomSheet = !showModalBottomSheet
            }
        ) {
            AboutBookSheet(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.padding.Normal)
                    .padding(it),
                description = details.description,
                chips = details.tags
            )
        }

    }


}

@Deprecated("Use FavoriteButton", replaceWith = ReplaceWith("FavoriteButton"))
@Composable
internal fun LikeButton(
    favorite: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Like"
        )
    }
}


private fun Chapter.getSubtitle(): String {
    var result = lastModifiedAt.toDate()
    if (!shouldRead()) {
        result += "• ${lastPageRead + 1}/${pagesCount + 1} 页"
    }
    return result
}

internal fun Chapter.getReadProgress(): Float =
    (lastPageRead + 1) / (pagesCount + 1).toFloat()


