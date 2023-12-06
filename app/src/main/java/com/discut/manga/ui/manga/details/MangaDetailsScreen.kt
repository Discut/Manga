package com.discut.manga.ui.manga.details

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.components.CustomModalBottomSheet
import com.discut.manga.components.manga.MangaInfoBox
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import com.discut.manga.ui.common.LoadingScreen
import com.discut.manga.ui.manga.details.component.AboutBookSheet
import com.discut.manga.ui.manga.details.component.InfoBoxType
import com.discut.manga.ui.manga.details.component.MoreInfoItem
import com.discut.manga.ui.manga.details.component.ShortInfoBox
import com.discut.manga.ui.manga.details.component.SwipeableActionCollection
import com.discut.manga.ui.manga.details.component.SwipeableChapterItem
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.util.toDate
import discut.manga.common.res.R

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
    Surface {
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
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    })
            }
        ) {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    MangaInfoBox(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.padding.ExtraLarge),
                        info = details
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
                            InfoBoxType.Title("Top", "Bottom"),
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
                                horizontal = MaterialTheme.padding.ExtraLarge,
                                vertical = MaterialTheme.padding.Large
                            )
                    )
                }
                item {
                    MoreInfoItem(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.padding.ExtraLarge)
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
                            .padding(horizontal = MaterialTheme.padding.ExtraLarge),
                        title = "Chapters",
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.ExpandMore,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        })
                }
                state.chapters.forEachIndexed { index, chapter ->
                    item {
                        var alpha by remember {
                            mutableStateOf(1f)
                        }
                        SwipeableChapterItem(
                            modifier = Modifier
                                .padding(
                                    horizontal = MaterialTheme.padding.ExtraLarge,
                                    vertical = MaterialTheme.padding.Normal
                                )
                                .alpha(alpha),
                            title = chapter.name,
                            subtitle = chapter.lastModifiedAt.toDate(),
                            leftAction = SwipeableActionCollection.Read {},

                            onSwipe = {
                                alpha = if (alpha == 1f) {
                                    MaterialTheme.alpha.Lowest
                                } else {
                                    MaterialTheme.alpha.Highest
                                }
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

