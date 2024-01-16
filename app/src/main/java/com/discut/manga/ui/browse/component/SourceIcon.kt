package com.discut.manga.ui.browse.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.discut.manga.R
import com.discut.manga.service.source.isLocal
import com.discut.manga.ui.browse.extensions.icon
import manga.source.Source

@Composable
fun SourceIcon(
    modifier: Modifier = Modifier,
    source: Source
) {
    when {
        source.isLocal() -> {
            Image(
                painter = painterResource(id = R.mipmap.ic_local_source),
                contentDescription = "Local Source",
                modifier = modifier.iconDefault()
            )
        }

        source.icon != null -> {
            Image(
                bitmap = source.icon!!,
                contentDescription = "Online Source",
                modifier = modifier.iconDefault(),
            )
        }

        else -> {
            Image(
                painter = painterResource(R.mipmap.ic_default_source),
                contentDescription = "Source",
                modifier = modifier.iconDefault(),
            )
        }
    }
}

private fun Modifier.iconDefault() = this
    .height(40.dp)
    .aspectRatio(1f)