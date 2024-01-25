package com.discut.manga.ui.settings.browse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.core.flowbus.FlowBus
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.event.NavigationEvent
import com.discut.manga.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSettingsScreen(
    vm: BrowseSettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by vm.uiState.collectAsStateWithLifecycle()
    val extensionRepos by state.extensionRepos.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    text = "Browse",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            TextPreferenceComponent(
                title = "Extensions repo",
                subTitle = "${extensionRepos.size} repo"
            ) {
                FlowBus.with<NavigationEvent>()
                    .post(
                        scope,
                        NavigationEvent(NavigationRoute.SettingsScreen.Browse.ExtensionRepos.route)
                    )
            }
        }
    }
}