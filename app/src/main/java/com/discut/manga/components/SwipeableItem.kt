package com.discut.manga.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.discut.manga.util.toPx
import com.discut.manga.util.withIOContext
import kotlinx.coroutines.delay
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import me.saket.swipe.SwipeableActionsState
import me.saket.swipe.rememberSwipeableActionsState
import kotlin.math.abs

@Composable
fun SwipeableItem(
    modifier: Modifier = Modifier,

    onClick: () -> Unit,
    leftWipeAction: com.discut.manga.components.SwipeAction? = null,
    rightWipeAction: com.discut.manga.components.SwipeAction? = null,

    onSwipe: ((SwipeDirection) -> Unit)? = null,

    content: @Composable RowScope.() -> Unit,
) {

    val swipeableActionsState = rememberSwipeableActionsState()

    var isState by remember {
        mutableStateOf(true)
    }
    var startAction = listOf<SwipeAction>()
    var endAction = listOf<SwipeAction>()

    rightWipeAction?.let {
        val rightDone = SwipeAction(
            icon = {
                if (rightWipeAction.shadowIcon != null) {
                    IconSlipBox(
                        state = swipeableActionsState,
                        topContent = {
                            rightWipeAction.icon()
                        },
                        bottomContent = {
                            rightWipeAction.shadowIcon.invoke()
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        rightWipeAction.icon()
                    }
                }
            },
            background = rightWipeAction.background,
            onSwipe = {
                isState = false
                rightWipeAction.onSwipe()
                onSwipe?.invoke(SwipeDirection.R)
            }
        )
        val undo = SwipeAction(
            icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.Undo),
            background = Color.hsv(42.12f, 1f, 1f),
            isUndo = true,
            onSwipe = { isState = true },
        )
        startAction = listOf(rightDone, undo)
    }

    leftWipeAction?.let {
        val leftDone = SwipeAction(
            icon = {
                if (leftWipeAction.shadowIcon != null) {
                    IconSlipBox(
                        orientation = 1,
                        state = swipeableActionsState,
                        topContent = {
                            leftWipeAction.icon()
                        },
                        bottomContent = {
                            leftWipeAction.shadowIcon.invoke()
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        leftWipeAction.icon()
                    }
                }
            },
            background = Color.Green,
            onSwipe = {
                leftWipeAction.onSwipe()
                isState = false
                onSwipe?.invoke(SwipeDirection.L)
            }
        )

        val redo = SwipeAction(
            icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.Redo),
            background = Color.hsv(42.12f, 1f, 1f),
            isUndo = true,
            onSwipe = { isState = true },
        )
        endAction = listOf(leftDone, redo)
    }



    SwipeableActionsBox(
        modifier =
        Modifier,
        swipeThreshold = swipeThreshold,
        startActions = startAction,
        endActions = endAction,
        state = swipeableActionsState
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick() }
                .then(modifier)
        ) {
            content.invoke(this)
        }
    }
}

@Composable
fun IconSlipBox(
    isChange: Boolean = false,

    state: SwipeableActionsState,
    orientation: Int = 0,

    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit
) {
    var swipeChange by remember {
        mutableStateOf(false)
    }
    val isShow by derivedStateOf { isChange || swipeChange }
    val swipeThresholdPx = swipeThreshold.toPx()

    LaunchedEffect(key1 = state.offset) {
        var oldOffset = 0f
        var checkNum = 0
        withIOContext {
            while (true) {
                val newOffset = abs(state.offset.value)
                // 检测是否滑动是否超过阈值
                if (newOffset < swipeThresholdPx) {
                    delay(CHECK_INTERVAL)
                    continue
                }
                // 检测滑动是否暂停
                if ((abs(oldOffset - newOffset) < SWIPE_INTERVAL)) {
                    checkNum++
                } else {
                    checkNum = 0
                }
                // 检测是否应该切换至第二个盒子
                if (checkNum >= SWIPE_STOP_DURATION * 1000 / CHECK_INTERVAL) {
                    swipeChange = true
                    checkNum = 0
                    break
                }
                oldOffset = newOffset
                delay(CHECK_INTERVAL)
            }
            // 切换回最初盒子
            delay(2000)
            swipeChange = false
        }
    }
    Box(
        modifier = Modifier
            .padding(16.dp),
        contentAlignment = orientation.takeIf { it == 0 }?.let { Alignment.CenterEnd }
            ?: Alignment.CenterStart
    ) {
        AnimatedVisibility(
            visible = !isShow,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { -it },
                animationSpec = animationFunction,
            ),
            exit = fadeOut() + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = animationFunction,
            )
        ) {
            topContent.invoke()
        }
        AnimatedVisibility(
            visible = isShow,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { -it },
                animationSpec = animationFunction,
            ),
            exit = fadeOut() + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = animationFunction,
            )
        ) {
            bottomContent.invoke()
        }
    }
}

data class SwipeAction(
    val icon: @Composable () -> Unit,
    val shadowIcon: (@Composable () -> Unit)? = null,
    val background: Color,
    val onSwipe: () -> Unit,
)

enum class SwipeDirection {
    R, L
}

private val swipeThreshold = 70.dp
private val animationFunction = tween<IntOffset>(400)
private const val SWIPE_INTERVAL = 10 // 滑动停止的检测长度（短时间内滑动的位移小于此则不会检测）
private const val SWIPE_STOP_DURATION = 1 // 滑动停止的检测时间 s
private const val CHECK_INTERVAL = 100L // 检测间隔
