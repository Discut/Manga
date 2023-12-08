package com.discut.manga.ui.manga.details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.discut.manga.components.preference.VerticalDivider
import com.discut.manga.theme.alpha

sealed interface InfoBoxType {
    data class Title(val top: String, val bottom: String) : InfoBoxType
    data class Icon(val bottom: String, val icon: @Composable (modifier: Modifier) -> Unit) :
        InfoBoxType
}

@Composable
fun ShortInfoBox(
    modifier: Modifier = Modifier,

    contexts: List<InfoBoxType> = emptyList()
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        contexts.forEachIndexed { index, context ->
            when (context) {
                is InfoBoxType.Title -> {
                    InfoBox(context.bottom) {
                        Text(text = context.top, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                    }
                }

                is InfoBoxType.Icon -> {
                    InfoBox("Preview") {
                        context.icon(it)
                    }
                }
            }
            if (index != contexts.lastIndex) {
                InfoBoxVerticalDivider()
            }
        }
    }

}

@Composable
fun InfoBox(
    bottomContent: String,
    topContent: @Composable (modifier: Modifier) -> Unit,
) {
    Box(
        modifier = Modifier
            .alpha(MaterialTheme.alpha.Normal)
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            topContent(Modifier.size(20.dp))
            Text(
                text = bottomContent, style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun InfoBoxVerticalDivider() {
    Column(modifier = Modifier.fillMaxHeight()) {
        Spacer(modifier = Modifier.weight(1f))
        VerticalDivider(modifier = Modifier.weight(2f))
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
@PreviewLightDark
fun PreviewShortInfoBox() {
    MaterialTheme {
        ShortInfoBox(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .height(50.dp),
            contexts = listOf(
                InfoBoxType.Icon("Preview") {
                    Icon(
                        modifier = it,
                        imageVector = Icons.Default.Preview,
                        contentDescription = ""
                    )
                },
                InfoBoxType.Title("Top", "Bottom"),
                InfoBoxType.Icon("Preview") {
                    Icon(
                        modifier = it,
                        imageVector = Icons.Default.Preview,
                        contentDescription = ""
                    )
                }
            )
        )
    }
}