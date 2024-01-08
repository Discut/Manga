package com.discut.manga.ui.manga.details.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.discut.manga.theme.padding
import kotlinx.coroutines.delay

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,

    onClick: () -> Unit,
    onAnimated: () -> Unit
) {
    var onShow by remember { mutableStateOf(true) }
    var init by remember { mutableStateOf(false) }
    if (!isFavorite) {
        onShow = true
    } else if (onShow && !init) {
        onShow=false
    }
    AnimatedContent(targetState = onShow, label = "favorite button animation") {
        if (it) {
            if (isFavorite) {
                LaunchedEffect(Unit) {
                    delay(620)
                    onAnimated()
                    onShow = false
                }
            }
            TextButton(modifier = modifier, onClick = {
                init = true
                onClick()
            }) {
                AnimatedContent(
                    targetState = isFavorite,
                    label = "favorite button animation",
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                slideInVertically(animationSpec = tween(220, delayMillis = 90)))
                            .togetherWith(
                                fadeOut(animationSpec = tween(90)) +
                                        slideOutVertically(
                                            animationSpec = tween(
                                                220,
                                                delayMillis = 90
                                            )
                                        )
                            )
                    },
                    contentAlignment = Alignment.Center
                ) {
                    if (it) {
                        Text(text = "已收藏")
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "favorite"
                            )
                            Spacer(modifier = Modifier.width(MaterialTheme.padding.Small))
                            Text(text = "收藏")
                        }

                    }
                }
            }
        }
    }
}