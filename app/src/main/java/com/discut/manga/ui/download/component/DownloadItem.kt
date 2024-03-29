package com.discut.manga.ui.download.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.discut.manga.components.preference.BasePreferenceComponent
import com.discut.manga.service.saver.download.model.Downloader
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import com.discut.manga.util.toPx
import discut.manga.data.download.DownloadState
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadItem(
    modifier: Modifier = Modifier,
    state: ReorderableLazyListState,
    downloader: Downloader,
    onCancel: () -> Unit,
    onPause: () -> Unit,
    onStart: () -> Unit,
    onRetry: () -> Unit
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
            onCancel()
        }
    }
    SwipeToDismissBox(modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        backgroundContent = {
            val direction =
                dismissState.dismissDirection ?: return@SwipeToDismissBox
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.LightGray
                    DismissValue.DismissedToEnd -> Color.hsv(
                        354.0F,
                        .1961F,
                        1.0F
                    )

                    DismissValue.DismissedToStart -> Color.Red
                },
                label = "swipe color"
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Outlined.Delete
                DismissDirection.EndToStart -> Icons.Default.Done
            }
            val iconTint by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToEnd -> Color.Red
                    else -> Color.Gray
                },
                label = "swipe color"
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 1f else 1.25f,
                label = "swipe scale"
            )
            val padding by animateDpAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToEnd -> 10.dp
                    else -> 0.dp
                }, label = ""
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    tint = iconTint,
                    contentDescription = "Localized description",
                    modifier = Modifier
                        .scale(scale)
                        .padding(start = padding)
                )
            }
        }) {
        Surface {
            BasePreferenceComponent(
                title = downloader.chapter.name,
                iconWidget = {
                    Icon(
                        modifier = Modifier
                            .detectReorder(state)
                            .padding(MaterialTheme.padding.Normal),
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = "Drag"
                    )
                },
                endWidget = {
                    var expanded by remember { mutableStateOf(false) }
                    when (downloader.status) {
                        is Downloader.DownloadState.Error -> {
                            IconButton(
                                onClick = { },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = Color.Red
                                )
                            }
                        }

                        else -> {
                            IconButton(
                                onClick = { expanded = !expanded },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "More",
                                )

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                ) {
                                    when (downloader.download.status) {
                                        DownloadState.NotInQueue, DownloadState.Completed, DownloadState.Error -> {


                                        }

                                        DownloadState.Waiting, DownloadState.Downloading -> {
                                            DropdownMenuItem(
                                                text = { Text(text = "Pause") },
                                                onClick = {
                                                    onPause()
                                                    expanded = false
                                                }
                                            )
                                        }

                                        DownloadState.InQueue -> {
                                            DropdownMenuItem(
                                                text = { Text(text = "Start") },
                                                onClick = {
                                                    onStart()
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                    DropdownMenuItem(text = { Text(text = "Retry") }, onClick = {
                                        onRetry()
                                        expanded = false
                                    })
                                    /*DropdownMenuItem(
                                        text = { Text(text = "Cancel") },
                                        onClick = {
                                            onCancel()
                                            expanded = false
                                        }
                                    )*/
                                }
                            }
                        }
                    }
                },
                subWidget = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = downloader.manga.title,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(MaterialTheme.alpha.Normal)
                        )

                        Text(
                            text = "${downloader.download.downloaded.size}/${downloader.pages?.size}",
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(MaterialTheme.alpha.Normal)
                        )
                    }
                    when (downloader.status) {
                        Downloader.DownloadState.Downloaded -> {}
                        Downloader.DownloadState.Downloading -> {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                progress = { downloader.download.downloaded.size.toFloat() / downloader.pages!!.size })
                        }

                        is Downloader.DownloadState.Error -> {
                            Text(
                                text = (downloader.status as Downloader.DownloadState.Error).msg,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Downloader.DownloadState.InQueue -> {
                            Text(
                                text = "In Queue", modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Downloader.DownloadState.Waiting -> {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                            )
                        }
                    }


                }
            )
        }
    }
}