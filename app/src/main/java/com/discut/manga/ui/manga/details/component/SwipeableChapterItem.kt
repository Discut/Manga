package com.discut.manga.ui.manga.details.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.discut.manga.components.SwipeAction
import com.discut.manga.components.SwipeDirection
import com.discut.manga.components.SwipeableItem
import com.discut.manga.theme.alpha

@Composable
fun SwipeableChapterItem(
    modifier: Modifier = Modifier,

    title: String,
    subtitle: String,
    visibleProgress: Boolean,

    progress: () -> Float,
    leftAction: SwipeableActionCollection? = null,
    rightAction: SwipeableActionCollection? = null,

    onClick: () -> Unit,
    onSwipe: (SwipeDirection) -> Unit = {},

    leftContent: (@Composable () -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null
) {
    var rightWipeAction: SwipeAction? by remember {
        mutableStateOf(
            leftAction?.action1
        )
    }
    var leftWipeAction: SwipeAction? by remember {
        mutableStateOf(
            rightAction?.action1
        )
    }

    SwipeableItem(
        modifier = modifier,
        rightWipeAction = rightWipeAction,
        leftWipeAction = leftWipeAction,
        onSwipe = {
            when (it) {
                SwipeDirection.R -> {
                    if (rightWipeAction == null) {
                        return@SwipeableItem
                    }
                    rightWipeAction = if (rightWipeAction == leftAction?.action1) {
                        leftAction?.action2
                    } else {
                        leftAction?.action1
                    }
                }

                SwipeDirection.L -> {
                    if (leftWipeAction == null) {
                        return@SwipeableItem
                    }
                    leftWipeAction = if (leftWipeAction == rightAction?.action1) {
                        rightAction?.action2
                    } else {
                        rightAction?.action1
                    }
                }
            }
            onSwipe(it)
        },
        onClick = onClick
    ) {
        leftContent?.invoke()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(MaterialTheme.alpha.Normal)
            )
            AnimatedVisibility(
                visible = visibleProgress,
                exit = shrinkVertically()
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        rightContent?.invoke()
    }
}

sealed class SwipeableActionCollection(
    val action1: SwipeAction,
    val action2: SwipeAction
) {
    data class Read(val onSwipe: () -> Unit) : SwipeableActionCollection(
        action1 = SwipeAction(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = "",
                )
            },
            shadowIcon = {
                Text(
                    text = "滑至黄色取消",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            background = Color.hsv(145.81f, .4526f, .7451f),
            onSwipe = onSwipe,
        ),
        action2 = SwipeAction(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = "",
                )
            },
            shadowIcon = {
                Text(
                    text = "滑至黄色取消",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            background = Color.hsv(145.81f, .4526f, .7451f),
            onSwipe = onSwipe,
        ),
    )
}