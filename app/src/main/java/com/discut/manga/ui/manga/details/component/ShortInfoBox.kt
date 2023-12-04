package com.discut.manga.ui.manga.details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.discut.manga.components.preference.VerticalDivider

@Composable
fun ShortInfoBox(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InfoBox()
        VerticalDivider()
        InfoBox()
    }

}

@Composable
fun InfoBox() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(modifier = Modifier.wrapContentSize(Alignment.Center)) {
            Icon(imageVector = Icons.Outlined.Image, contentDescription = "")
            Text(text = "Preview", style = MaterialTheme.typography.bodySmall)
        }
    }

}

@Composable
@PreviewLightDark
fun PreviewShortInfoBox() {
    MaterialTheme {
        ShortInfoBox(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        )
    }
}