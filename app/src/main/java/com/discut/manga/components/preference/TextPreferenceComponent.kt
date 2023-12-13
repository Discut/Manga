package com.discut.manga.components.preference

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.discut.manga.theme.MangaTheme
import com.discut.manga.theme.padding

@Composable
fun TextPreferenceComponent(
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.padding(vertical = MaterialTheme.padding.Default),
    title: String,
    subTitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    endWidget: @Composable ((PaddingValues) -> Unit)? = null,
    onPreferenceClick: (() -> Unit)? = null
) {
    BasePreferenceComponent(
        modifier = modifier
            .padding(horizontal = MaterialTheme.padding.Normal),
        title = title,
        iconWidget = {
            if (icon != null) {
                Icon(
                    modifier = Modifier.padding(it),
                    imageVector = icon,
                    tint = iconTint,
                    contentDescription = ""
                )
            }
        },
        subWidget = {
            if (subTitle != null) {
                Text(
                    text = subTitle,
                    modifier = Modifier
                        .alpha(0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 5,
                )
            }
        },
        onClick = onPreferenceClick,
        endWidget = endWidget
    )
}

@Preview(
    showBackground = true,
    name = "Light"
)
@Composable
private fun TextPreferenceComponentPreview() {
    MangaTheme {
        Column {
            TextPreferenceComponent(
                title = "Title",
                subTitle = "SubTitle",
            )

            TextPreferenceComponent(
                title = "Title with icon",
                subTitle = "SubTitle",
                icon = Icons.Default.Preview
            )
        }

    }
}