package com.discut.manga.ui.history.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.discut.manga.components.domain.MangaCoverInfo
import com.discut.manga.components.manga.MangaCover
import com.discut.manga.domain.history.MangaChapterHistory
import com.discut.manga.theme.MangaTheme
import com.discut.manga.theme.padding
import com.discut.manga.util.toPx
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    itemType: HistoryItemType = HistoryItemType.LOOSE,
    history: MangaChapterHistory,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val positionalThreshold = 80.dp.toPx()
    val dismissState = rememberDismissState(
        positionalThreshold = {
            positionalThreshold
        },
        confirmValueChange = {
            it != DismissValue.DismissedToStart
        }
    )
    LaunchedEffect(key1 = dismissState.currentValue) {
        if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
            onDelete()
        }
    }

    when (itemType) {
        HistoryItemType.COMPACT -> HistoryCompactItem(
            modifier = modifier.height(50.dp),
            history = history,
            paddingValues = paddingValues,
            dismissState = dismissState,
            onClick = onClick
        )

        HistoryItemType.LOOSE -> HistoryLooseItem(
            modifier = modifier.height(100.dp),
            history = history,
            paddingValues = paddingValues,
            dismissState = dismissState,
            onClick = onClick
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryCompactItem(
    modifier: Modifier = Modifier,
    history: MangaChapterHistory,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    dismissState: DismissState,
    onClick: () -> Unit
) {
    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        backgroundContent = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismissBox
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.LightGray
                    DismissValue.DismissedToEnd -> Color.Red
                    DismissValue.DismissedToStart -> Color.Red
                },
                label = "swipe color"
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Delete
                DismissDirection.EndToStart -> Icons.Default.Done
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
                label = "swipe scale"
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    icon,
                    contentDescription = "Localized description",
                    modifier = Modifier.scale(scale)
                )
            }
        }
    ) {
        val isShowOutline by remember {
            derivedStateOf {
                dismissState.targetValue != DismissValue.Default
            }
        }
        Box(modifier = Modifier.clickable { onClick() }) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                MangaCover.SQUARE(
                    info = MangaCoverInfo(
                        coverUrl = history.thumbnailUrl,
                        title = history.mangaTitle,
                    )
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = MaterialTheme.padding.Default),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = history.mangaTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${history.chapterName} • ${SimpleDateFormat("hh: mm").format(history.readAt)}",
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedVisibility(
                    visible = isShowOutline,
                    exit = fadeOut()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.3.dp
                    )
                }
                AnimatedVisibility(
                    visible = isShowOutline,
                    exit = fadeOut()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.3.dp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryLooseItem(
    modifier: Modifier = Modifier,
    history: MangaChapterHistory,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    dismissState: DismissState,
    onClick: () -> Unit
) {
    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        backgroundContent = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismissBox
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.LightGray
                    DismissValue.DismissedToEnd -> Color.Red
                    DismissValue.DismissedToStart -> Color.Red
                },
                label = "swipe color"
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Delete
                DismissDirection.EndToStart -> Icons.Default.Done
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
                label = "swipe scale"
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    icon,
                    contentDescription = "Localized description",
                    modifier = Modifier.scale(scale)
                )
            }
        }
    ) {
        val isShowOutline by remember {
            derivedStateOf {
                dismissState.targetValue != DismissValue.Default
            }
        }
        Box(modifier = Modifier.clickable { onClick() }) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                MangaCover.BOOK(
                    info = MangaCoverInfo(
                        coverUrl = history.thumbnailUrl,
                        title = history.mangaTitle,
                    )
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = MaterialTheme.padding.Default),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = history.mangaTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${history.chapterName} • ${SimpleDateFormat("hh: mm").format(history.readAt)}",
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedVisibility(
                    visible = isShowOutline,
                    exit = fadeOut()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.3.dp
                    )
                }
                AnimatedVisibility(
                    visible = isShowOutline,
                    exit = fadeOut()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.3.dp
                    )
                }
            }
        }
    }
}

enum class HistoryItemType {
     LOOSE,COMPACT,
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun HistoryItemPreview() {
    MangaTheme {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                HistoryItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    history = MangaChapterHistory(
                        historyId = 1,
                        mangaId = 1,
                        mangaTitle = "Manga Title",
                        chapterName = "Chapter Name",
                        thumbnailUrl = "",
                        chapterId = 1,
                        readAt = System.currentTimeMillis()
                    ),
                    onDelete = {

                    }
                ) {

                }
            }

        }
    }
}