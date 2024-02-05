package com.discut.manga.components.scaffold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.padding


@Composable
fun ItemActions(
    actionsBuilder: ItemAction.() -> Unit,
) {
    val actions = ItemActionImpl().apply(actionsBuilder).actions
    ItemActions(actions = actions)
}

@Composable
private fun ItemActions(
    actions: List<ItemActionType>,
) {
    var showMenu by remember { mutableStateOf(false) }

    actions.filterIsInstance<ItemActionType.IconAction>().map {
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

    val overflowActions = actions.filterIsInstance<ItemActionType.OverflowAction>()
    if (overflowActions.isEmpty()) {
        return
    }
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
            overflowActions.forEach {
                when (it) {
                    is ItemActionType.OverflowAction.OverflowRadioAction -> {
                        var checked by remember { mutableStateOf(it.checked) }
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(it.title, fontWeight = FontWeight.Normal)
                                    Spacer(modifier = Modifier.padding(horizontal = MaterialTheme.padding.Default))
                                    RadioButton(selected = checked, onClick = null)
                                }
                            },
                            onClick = {
                                checked = !checked
                                it.onClick(checked)
                                showMenu = false
                            }
                        )
                    }

                    is ItemActionType.OverflowAction.OverflowTextAction -> {
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
}


sealed interface ItemActionType {
    data class IconAction(
        var title: String,
        var icon: ImageVector,
        var iconTint: Color? = null,
        var onClick: () -> Unit,
        var enabled: Boolean = true,
    ) : ItemActionType

    sealed interface OverflowAction : ItemActionType {
        data class OverflowTextAction(
            var title: String,
            var onClick: () -> Unit,
        ) : OverflowAction

        data class OverflowRadioAction(
            var title: String,
            var onClick: (Boolean) -> Unit,
            var checked: Boolean = false,
        ) : OverflowAction
    }
}

interface ItemAction {

    fun toIconAction(
        builder: ItemActionType.IconAction.() -> Unit,
    )

    fun toOverflowTextAction(
        builder: ItemActionType.OverflowAction.OverflowTextAction.() -> Unit,
    )

    fun toOverflowRadioAction(
        builder: ItemActionType.OverflowAction.OverflowRadioAction.() -> Unit,
    )
}

internal class ItemActionImpl : ItemAction {

    internal val actions = mutableListOf<ItemActionType>()

    override fun toIconAction(builder: ItemActionType.IconAction.() -> Unit) {
        ItemActionType.IconAction(
            title = "",
            icon = Icons.Default.Close,
            onClick = {})
            .apply(builder)
            .apply(actions::add)
    }

    override fun toOverflowTextAction(builder: ItemActionType.OverflowAction.OverflowTextAction.() -> Unit) {
        ItemActionType.OverflowAction.OverflowTextAction(
            title = "",
            onClick = {})
            .apply(builder)
            .apply(actions::add)
    }

    override fun toOverflowRadioAction(builder: ItemActionType.OverflowAction.OverflowRadioAction.() -> Unit) {
        ItemActionType.OverflowAction.OverflowRadioAction(
            title = "",
            onClick = {})
            .apply(builder)
            .apply(actions::add)
    }
}