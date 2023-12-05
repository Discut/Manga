package com.discut.manga.ui.manga.details.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.alpha
import kotlinx.coroutines.delay
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import me.saket.swipe.rememberSwipeableActionsState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableChapterItem(
    modifier: Modifier = Modifier,

    title: String,
    subtitle: String,

    onClick: () -> Unit,

    leftContent: (@Composable () -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null
) {
    var isState by remember {
        mutableStateOf(false)
    }

    val current = LocalContext.current

    val done = SwipeAction(
        icon = rememberVectorPainter(Icons.Outlined.Done),
        background = Color.Green,
        onSwipe = {
            Toast.makeText(current, "Long click", Toast.LENGTH_SHORT).show()
        }
    )
    val animationFunction = tween<IntOffset>(400)
    val done2 = SwipeAction(
        icon = {
            LaunchedEffect(key1 = Unit) {
                delay(1000)
                isState = !isState
            }
            Box(
                modifier = Modifier
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                AnimatedVisibility(
                    visible = isState,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = animationFunction,
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = animationFunction,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = "",
                    )
                }
                AnimatedVisibility(
                    visible = !isState,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = animationFunction,
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = animationFunction,
                    )
                ) {
                    Text(text = "向后滑动取消", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        background = Color.Green,
        onSwipe = {
            isState = true
        }
    )

    val undo = SwipeAction(
        icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.Undo),
        background = Color.hsv(42.12f, 1f, 1f),
        isUndo = true,
        onSwipe = { isState = true },
    )

    val swipeableActionsState = rememberSwipeableActionsState()
    // TODO 尝试使用 rememberSwipeableActionsState 去获取swipe状态，用于检测滑动是否暂停
    SwipeableActionsBox(
        modifier =
        Modifier,
        swipeThreshold = swipeThreshold,
        startActions = listOf(done2, undo),
        endActions = listOf(done, undo),
        state = swipeableActionsState
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick() }
                .then(modifier)
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
            }
            rightContent?.invoke()
        }
    }
}

private val swipeThreshold = 70.dp