package com.discut.manga.components.preference

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.MangaTheme

@Composable
fun SwitchPreferenceComponent(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String? = null,
    icon: ImageVector? = null,
    state: Boolean = false,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onSwitchClick: ((value: Boolean) -> Unit)? = null,
    onPreferenceClick: ((oldValue: Boolean, newValue: Boolean) -> Unit)? = null,
) {
    TextPreferenceComponent(
        modifier = modifier,
        title = title,
        subTitle = subTitle,
        icon = icon,
        iconTint = iconTint,
        endWidget = {
            Row(
                modifier = Modifier
                    .requiredHeight(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onSwitchClick != null) {
                    VerticalDivider(
                        Modifier
                            .height(32.0.dp)
                            .padding(horizontal = 16.dp)
                    )
                }
                Switch(
                    checked = state,
                    onCheckedChange = onSwitchClick
                )
            }
        }
    ) {
        onPreferenceClick?.invoke(state, !state)
    }
}

@Preview(
    showBackground = true,
    name = "Light"
)
@Composable
private fun SwitchPreferenceComponentPreview() {
    var state by remember {
        mutableStateOf(false)
    }
    MangaTheme {
        Column {
            SwitchPreferenceComponent(
                title = "Switch preference",
                subTitle = "Sub title",
                state = state,
                icon = Icons.Default.Preview,
            ) { old, new ->
                state = !state
            }
            SwitchPreferenceComponent(
                title = "Switch preference",
                subTitle = "Sub title",
                state = state,
            ) { old, new ->
                state = !state
            }
            SwitchPreferenceComponent(
                title = "Switch preference",
                subTitle = "Sub title",
                state = state,
                icon = Icons.Default.Preview,
                onSwitchClick = { state = !state }
            ) { old, new ->
               // state = !state
            }
            SwitchPreferenceComponent(
                title = "Switch preference",
                subTitle = "Sub title",
                state = state,
                onSwitchClick = { state = !state }
            ) { old, new ->
               // state = !state
            }
        }
    }
}