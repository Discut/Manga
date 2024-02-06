package com.discut.manga.ui.reader.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import discut.manga.common.res.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderNavigationTopBar(
    modifier: Modifier = Modifier,
    onBackActionClick: () -> Unit,
    title: String?,
    subtitle: String?,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color? =null,
    scrollBehavior: TopAppBarScrollBehavior? = null,

    onTitleClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onBackActionClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            },
            title = {
                Column {
                    title?.let {
                        Text(
                            modifier = Modifier.clickable {
                                onTitleClick()
                            },
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.basicMarquee(
                                delayMillis = 2_000,
                            )
                        )
                    }

                }
            },
            actions = actions,
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = backgroundColor
                    ?: MaterialTheme.colorScheme.surfaceColorAtElevation(
                        elevation = /*if (isActionMode) 3.dp else*/ 0.dp,
                    ),
            ),
            scrollBehavior = scrollBehavior,
        )
    }
}