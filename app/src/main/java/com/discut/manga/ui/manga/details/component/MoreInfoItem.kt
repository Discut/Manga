package com.discut.manga.ui.manga.details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import com.discut.manga.theme.padding

@Composable
fun MoreInfoItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .then(modifier)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.padding.Medium)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 18.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.NavigateNext,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        content?.invoke(this)
    }
}


@PreviewLightDark
@Composable
fun MoreInfoItemPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            MoreInfoItem(
                title = "MoreInfoItem test title",
                onClick = {}
            ) {

            }
        }
    }
}