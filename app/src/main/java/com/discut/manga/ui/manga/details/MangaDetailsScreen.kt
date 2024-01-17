package com.discut.manga.ui.manga.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Downloading
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.twotone.CropRotate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.core.flowbus.FlowBus
import com.discut.core.mvi.CollectSideEffect
import com.discut.manga.components.CustomModalBottomSheet
import com.discut.manga.components.SwipeDirection
import com.discut.manga.components.domain.toMangaCoverInfo
import com.discut.manga.components.indicator.AppLinearIndicator
import com.discut.manga.components.manga.MangaCover
import com.discut.manga.components.manga.MangaInfoBox
import com.discut.manga.components.scaffold.AppBarActions
import com.discut.manga.data.manga.isLocal
import com.discut.manga.data.shouldRead
import com.discut.manga.navigation.NavigationRoute
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import com.discut.manga.ui.categories.NewCategory
import com.discut.manga.ui.common.LoadingScreen
import com.discut.manga.ui.main.domain.ToRouteEvent
import com.discut.manga.ui.manga.ChapterScope
import com.discut.manga.ui.manga.details.component.AboutBookSheet
import com.discut.manga.ui.manga.details.component.AddToFavoriteSheet
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
import discut.manga.data.download.DownloadState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    mangaId: Long,
    vm: MangaDetailsViewModel = hiltViewModel(),

    onBackPressed: () -> Unit,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val manga by state.manga.collectAsState()
    LaunchedEffect(key1 = mangaId) {
        vm.sendEvent(MangaDetailsEvent.Init(mangaId))
    }
    if (manga == null) {
        LoadingScreen()
        return
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val addToFavoriteSheetState = rememberModalBottomSheetState()

    var isShowModalBottomSheet by remember {
        mutableStateOf(false)
    }
    var isShowFavoriteSheet by remember {
        mutableStateOf(false)
    }
    var isShowAddDialog by remember {
        mutableStateOf(false)
    }

    /**
     * 收藏按钮动画是否停止
     */
    var isFavoriteAnimationStopped by remember {
        mutableStateOf(true)
    }
    val isShowFavoriteOnAppbar by remember {
        derivedStateOf {
            manga?.favorite ?: false && isFavoriteAnimationStopped
        }
    }
    val details = manga!!.toMangaDetails()
    val chapterListState = rememberLazyListState()
    val isShowReadFloatButton by remember {
        derivedStateOf { chapterListState.firstVisibleItemIndex > 3 }
    }
    val isShowComplexTitle by remember {
        derivedStateOf { chapterListState.firstVisibleItemIndex > 0 }
    }
    val chapters by state.chapters.collectAsState()

    val collapseAddToFavoriteSheet = {
        scope.launch { addToFavoriteSheetState.hide() }.invokeOnCompletion {
            if (!addToFavoriteSheetState.isVisible) {
                isShowFavoriteSheet = false
            }
        }
    }
    handleEffect(vm = vm, snackbarHostState = snackbarHostState)
    Surface {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                                manga?.let {
                                    vm.sendEvent {
                                        MangaDetailsEvent.FavoriteManga(
                                            it.copy(
                                                category = null
                                            )
                                        )
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "favorite"
                                )
                            }
                        }
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
                    enter = slideInVertically(initialOffsetY = { 2 * it }),
                    exit = slideOutVertically { it * 2 }
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
                        // println("滑动到了第 $index 项")
                    }
            }
            AppLinearIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(pv), isVisible = state.isLoading
            )
            LazyColumn(
                modifier = Modifier.padding(top = pv.calculateTopPadding()),
                state = chapterListState,
                contentPadding = PaddingValues(
                    bottom = pv.calculateTopPadding(),
                )
            ) {
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
                            InfoBoxType.Title(chapters.size.toString(), "章"),
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
                    val history by state.currentHistory.collectAsStateWithLifecycle()
                    Row(modifier = Modifier.padding(horizontal = MaterialTheme.padding.Normal)) {
                        FilledIconButton(
                            modifier = Modifier.weight(3f),
                            onClick = { vm.sendEvent(MangaDetailsEvent.StartToRead) }
                        ) {
                            when (history) {
                                null -> Text(text = "开始阅读")
                                else -> Text(text = "继续 ${history!!.chapterName}")
                            }
                        }
                        Spacer(modifier = Modifier.width(MaterialTheme.padding.Normal))
                        FavoriteButton(
                            modifier = Modifier.weight(1f),
                            isFavorite = manga?.favorite ?: false,
                            onClick = {
                                isShowFavoriteSheet = true
                            },
                            onAnimated = { isFavoriteAnimationStopped = true })
                    }
                }
                item {
                    MoreInfoItem(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.padding.Normal)
                            .padding(bottom = MaterialTheme.padding.Medium),
                        title = "关于此漫画",
                        onClick = {
                            isShowModalBottomSheet = true
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
                chapters.map { it.chapter }.forEachIndexed { index, c ->
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

                            rightContent = {
                                if (manga?.isLocal() == true) {
                                    return@SwipeableChapterItem
                                }
                                DownloadIcon(chapters[index],
                                    onDownloadClick = {
                                        vm.sendEvent {
                                            MangaDetailsEvent.DownloadChapter(manga!!, chapter)
                                        }
                                    },
                                    onProgressingClick = {

                                    },
                                    onDownloadedClick = {

                                    }
                                )
                            },

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
            sheetState = addToFavoriteSheetState,
            isShow = isShowModalBottomSheet,
            onDismissRequest = {
                isShowModalBottomSheet = !isShowModalBottomSheet
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
        if (isShowFavoriteSheet) {
            AddToFavoriteSheet(
                categories = state.categories,
                sheetState = addToFavoriteSheetState,
                onAddClick = { isShowAddDialog = true },
                onConfirm = { id ->
                    manga?.let {
                        vm.sendEvent {
                            MangaDetailsEvent.FavoriteManga(
                                it.copy(
                                    category = id
                                )
                            )
                        }
                    }
                    isFavoriteAnimationStopped = false
                    collapseAddToFavoriteSheet()
                },
                onEditClick = {
                    FlowBus.with<ToRouteEvent>()
                        .post(
                            scope,
                            ToRouteEvent(NavigationRoute.CategoryScreen.route, popup = false)
                        )
                    collapseAddToFavoriteSheet()
                },
                onDismissRequest = {
                    isShowFavoriteSheet = false
                }
            )
        }
        if (isShowAddDialog) {
            NewCategory(
                isFreeCategoryName = {
                    !state.categories.any { c -> c.name == it }
                },
                onConfirm = {
                    vm.sendEvent(MangaDetailsEvent.AddNewCategory(it))
                    isShowAddDialog = false
                }) {
                isShowAddDialog = false
            }
        }


    }


}

@Composable
private fun handleEffect(
    vm: MangaDetailsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    vm.CollectSideEffect {
        when (it) {
            is MangaDetailsEffect.JumpToRead -> {
                ReaderActivity.startActivity(
                    context,
                    it.mangaId,
                    it.chapterId
                )
            }

            is MangaDetailsEffect.NetworkError -> {
                snackbarHostState.showSnackbar(it.msg)
            }
        }
    }
}

@Composable
private fun DownloadIcon(
    chapterScope: ChapterScope,
    onDownloadClick: () -> Unit,
    onProgressingClick: () -> Unit,
    onDownloadedClick: () -> Unit
) {
    val downloadState by chapterScope.downloadState.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalContentColor provides Color.Black.copy(
            alpha = MaterialTheme.alpha.Lowest
        )
    ) {
        when (downloadState) {
            DownloadState.Error -> {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "")
                }
            }

            DownloadState.NotInQueue -> {
                IconButton(onClick = onDownloadClick) {
                    Icon(
                        imageVector = Icons.Outlined.Downloading,
                        contentDescription = "download"
                    )
                }
            }

            DownloadState.Waiting, DownloadState.Downloading, DownloadState.InQueue -> {
                IconButton(onClick = onProgressingClick) {
                    Icon(
                        imageVector = Icons.TwoTone.CropRotate,
                        contentDescription = "downloading"
                    )
                }
            }

            DownloadState.Completed -> {
                IconButton(onClick = onDownloadedClick) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "downloaded"
                    )
                }
            }
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


