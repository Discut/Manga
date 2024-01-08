package com.discut.manga.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp


@Composable
fun AppBarActions(
    actionsBuilder: AppBarAction.() -> Unit,
) {
    val actions = AppBarActionImpl().apply(actionsBuilder).actions
    AppBarActions(actions = actions)
}

@Composable
private fun AppBarActions(
    actions: List<AppBarActionType>,
) {
    var showMenu by remember { mutableStateOf(false) }

    actions.filterIsInstance<AppBarActionType.IconAction>().map {
        IconButton(
            onClick = it.onClick,
            enabled = it.enabled,
        ) {
            Icon(
                imageVector = it.icon,
                tint = it.iconTint ?: LocalContentColor.current,
                contentDescription = it.title,
            )
        }
    }

    val overflowActions = actions.filterIsInstance<AppBarActionType.OverflowAction>()
    if (overflowActions.isNotEmpty()) {
        var overflowMenuOffset by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        Box {
            IconButton(
                onClick = { showMenu = !showMenu },
                modifier = Modifier.onGloballyPositioned {
                    overflowMenuOffset = with(density) {
                        (-it.size.height).toDp()
                    }
                }
            ) {
                Icon(
                    Icons.Outlined.MoreVert,
                    contentDescription = "More functions",
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(0.dp, overflowMenuOffset),
            ) {
                overflowActions.map {
                    DropdownMenuItem(
                        onClick = {
                            it.onClick()
                            showMenu = false
                        },
                        text = { Text(it.title, fontWeight = FontWeight.Normal) },
                    )
                }
            }
        }

    }
}


sealed interface AppBarActionType {
    data class IconAction(
        var title: String,
        var icon: ImageVector,
        var iconTint: Color? = null,
        var onClick: () -> Unit,
        var enabled: Boolean = true,
    ) : AppBarActionType

    data class OverflowAction(
        var title: String,
        var onClick: () -> Unit,
    ) : AppBarActionType
}

interface AppBarAction {

    fun toIconAction(
        builder: AppBarActionType.IconAction.() -> Unit,
    )

    fun toOverflowAction(
        builder: AppBarActionType.OverflowAction.() -> Unit,
    )
}

internal class AppBarActionImpl : AppBarAction {

    internal val actions = mutableListOf<AppBarActionType>()

    override fun toIconAction(builder: AppBarActionType.IconAction.() -> Unit) {
        AppBarActionType.IconAction(
            title = "",
            icon = Icons.Default.Close,
            onClick = {})
            .apply(builder)
            .apply(actions::add)
    }

    override fun toOverflowAction(builder: AppBarActionType.OverflowAction.() -> Unit) {
        AppBarActionType.OverflowAction(
            title = "",
            onClick = {})
            .apply(builder)
            .apply(actions::add)
    }
}