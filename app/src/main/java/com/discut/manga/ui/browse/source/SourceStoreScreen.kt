package com.discut.manga.ui.browse.source

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.discut.core.flowbus.observeEvent
import com.discut.manga.components.preference.BasePreferenceComponent
import com.discut.manga.components.scaffold.ItemActions
import com.discut.manga.components.scaffold.SearchAppToolbar
import com.discut.manga.data.source.Extension
import com.discut.manga.event.ExtensionChangeEvent
import com.discut.manga.theme.padding
import com.discut.manga.ui.common.LoadingScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceStoreScreen(
    modifier: Modifier = Modifier,
    vm: SourceStoreViewModel = hiltViewModel()
) {
    val current = LocalContext.current
    var queryKey by remember { mutableStateOf("") }
    var isOnlyInstalled by remember { mutableStateOf(false) }
    var isOnlyRemote by remember { mutableStateOf(false) }
    val state by vm.uiState.collectAsStateWithLifecycle()

    LocalLifecycleOwner.current.observeEvent<ExtensionChangeEvent> {
        when (it) {
            is ExtensionChangeEvent.Added,
            is ExtensionChangeEvent.Removed,
            is ExtensionChangeEvent.Replace -> {
                vm.sendEvent {
                    SourceStoreEvent.Refresh
                }
            }
        }
    }

    if (state.loadState is LoadState.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            LoadingScreen(
                modifier = Modifier.fillMaxHeight(0.5f),
                placeholderText = "Loading extensions..."
            )
        }
        return
    }
    val loadExtensions by
    (state.loadState as LoadState.Success).extensionsStateFlow.collectAsStateWithLifecycle()
    SearchAppToolbar(
        defaultSearchKey = queryKey,
        titleContent = {
            Text(text = "Extensions")
        },
        onChangeSearchKey = {
            queryKey = it
        },
        actions = {
            ItemActions {
                toOverflowTextAction {
                    title = "Refresh"
                    onClick = {
                        vm.sendEvent {
                            SourceStoreEvent.Refresh
                        }
                    }
                }
                toOverflowRadioAction {
                    title = "Only installed"
                    checked = isOnlyInstalled
                    onClick = {
                        isOnlyRemote = false
                        isOnlyInstalled = !isOnlyInstalled
                    }
                }
                toOverflowRadioAction {
                    title = "Only Remote"
                    checked = isOnlyRemote
                    onClick = {
                        isOnlyInstalled = false
                        isOnlyRemote = !isOnlyRemote
                    }
                }
            }
        },
        windowInsets = WindowInsets.captionBar
    )
    val extensions by remember {
        derivedStateOf {
            loadExtensions
                .filter { it.name.contains(queryKey) }
                .filter { !isOnlyInstalled || it is Extension.LocalExtension }
                .filter { !isOnlyRemote || it is Extension.RemoteExtension } /*+ listOf(
                Extension.LocalExtension.Error(
                    name = "test",
                    version = "1.0.0",
                    versionCode = 2,
                    pkg = "test",
                    error = Exception(),
                    icon = null,
                    pkgFactory = null,
                    sources = emptyList(),
                    msg = null,
                )
            )*/
        }
    }
    LazyColumn(
        modifier = modifier
    ) {
        items(items = extensions, key = { it.pkg }) { extension ->
            ExtensionItem(
                modifier = Modifier.animateItemPlacement(),
                extension = extension,
                onUninstall = {
                    vm.sendEvent {
                        SourceStoreEvent.UninstallExtension(it)
                    }
                },
                onInfo = {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.parse("package:${it.pkg}")
                        current.startActivity(this)
                    }
                },
                onInstall = {
                    vm.sendEvent {
                        SourceStoreEvent.InstallExtension(it)
                    }
                }
            )
        }
    }
}

@Composable
private fun ExtensionItem(
    modifier: Modifier = Modifier,
    extension: Extension,

    onInfo: (Extension.LocalExtension.Success) -> Unit,
    onUninstall: (Extension.LocalExtension.Success) -> Unit,
    onInstall: (Extension.RemoteExtension) -> Unit
) {
    when (extension) {
        is Extension.LocalExtension.Error -> {
            BasePreferenceComponent(
                modifier = modifier
                    .padding(
                        start = MaterialTheme.padding.Normal,
                        end = MaterialTheme.padding.Small
                    ),
                title = extension.name,
                subWidget = {
                    Text(
                        text = "${extension.version} / ${extension.pkg}",
                        modifier = Modifier
                            .alpha(0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 5,
                    )
                },
                iconWidget = {
                    ExtensionIcon(
                        extension = extension,
                        modifier = Modifier
                            .padding(it)
                            .iconDefault()
                    )
                },
                endWidget = {
                    ItemActions {
                        toOverflowTextAction {
                            title = "Uninstall"
                            /*onClick = {
                                onUninstall(extension)
                            }*/
                        }
                    }
                }
            )
        }

        is Extension.LocalExtension.Success -> {
            BasePreferenceComponent(
                modifier = modifier
                    .padding(
                        start = MaterialTheme.padding.Normal,
                        end = MaterialTheme.padding.Small
                    ),
                title = extension.name,
                subWidget = {
                    Text(
                        text = "${extension.version} / ${extension.pkg}",
                        modifier = Modifier
                            .alpha(0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 5,
                    )
                },
                iconWidget = {
                    ExtensionIcon(
                        extension = extension,
                        modifier = Modifier
                            .padding(it)
                            .iconDefault()
                    )
                },
                endWidget = {
                    ItemActions {
                        toOverflowTextAction {
                            title = "Uninstall"
                            onClick = {
                                onUninstall(extension)
                            }
                        }
                        toOverflowTextAction {
                            title = "Info"
                            onClick = {
                                onInfo(extension)
                            }
                        }
                    }
                }
            )
        }

        is Extension.RemoteExtension -> {
            BasePreferenceComponent(
                modifier = modifier
                    .padding(
                        start = MaterialTheme.padding.Normal,
                        end = MaterialTheme.padding.Small
                    ),
                title = extension.name,
                subWidget = {
                    Text(
                        text = "${extension.version} / ${extension.pkg}",
                        modifier = Modifier
                            .alpha(0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 5,
                    )
                },
                iconWidget = {
                    ExtensionIcon(
                        extension = extension,
                        modifier = Modifier
                            .padding(it)
                            .iconDefault()
                    )
                },
                endWidget = {
                    val extensionState by
                    extension.state.collectAsStateWithLifecycle()

                    when (extensionState) {
                        Extension.RemoteExtension.State.WAITING -> {
                            ItemActions {
                                toIconAction {
                                    icon = Icons.Outlined.Download
                                    onClick = {
                                        onInstall(extension)
                                    }
                                }
                            }
                        }

                        Extension.RemoteExtension.State.DOWNLOADING,
                        Extension.RemoteExtension.State.DOWNLOADED,
                        Extension.RemoteExtension.State.INSTALLING,
                        Extension.RemoteExtension.State.INSTALLED -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(end = MaterialTheme.padding.Medium),
                                strokeWidth = 2.dp
                            )
                        }

                        Extension.RemoteExtension.State.ERROR -> {}
                    }
                }
            )
        }
    }
}

@Composable
private fun ExtensionIcon(
    modifier: Modifier = Modifier,
    extension: Extension
) {
    when (extension) {
        is Extension.LocalExtension.Error -> {
            Surface(
                modifier = Modifier
                    .then(modifier)
                    .clip(MaterialTheme.shapes.extraSmall),
                color = Color.hsv(1.36f, .7686f, .898f),
            ) {
                Icon(
                    painter = painterResource(id = com.discut.manga.R.drawable.sharp_exclamation_24),
                    contentDescription = "error",
                    tint = Color.White
                )
            }
        }

        is Extension.LocalExtension.Success -> {
            val palette by remember {
                extension.icon?.let {
                    mutableStateOf(Palette.from(extension.icon.toBitmap()).generate())
                } ?: mutableStateOf<Palette?>(null)
            }
            Surface(
                modifier = Modifier
                    .then(modifier)
                    .clip(MaterialTheme.shapes.extraSmall),
                color = Color(palette?.getDominantColor(0x00000000) ?: 0x0000000000)

            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = extension.icon,
                    contentDescription = "icon"
                )
            }
        }

        is Extension.RemoteExtension -> {
            Surface(
                modifier = Modifier
                    .then(modifier)
                    .clip(MaterialTheme.shapes.extraSmall),
                shadowElevation = 4.dp
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = extension.iconUrl,
                    contentDescription = "icon"
                )
            }
        }
    }
}

private fun Modifier.iconDefault() = this
    .height(32.dp)
    .aspectRatio(1f)