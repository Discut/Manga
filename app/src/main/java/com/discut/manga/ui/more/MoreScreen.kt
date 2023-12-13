package com.discut.manga.ui.more

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discut.core.flowbus.FlowBus
import com.discut.core.mvi.CollectSideEffect
import com.discut.manga.components.preference.SwitchPreferenceComponent
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.navigation.NavigationEvent
import com.discut.manga.navigation.NavigationRoute
import com.discut.manga.ui.main.domain.ToRouteEvent

@Composable
fun MoreScreen() {
    val viewModel: MoreScreenViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val rememberCoroutineScope = rememberCoroutineScope()

    viewModel.CollectSideEffect {
        when (it) {
            is MoreScreenEffect.SecurityModeChange -> {
                //state.enableSecurityMode = it.enable
            }
        }
    }
    LazyColumn {
        item {
            SwitchPreferenceComponent(
                title = "隐私模式", subTitle = "隐私模式",
                //icon = Icons.Default.Security,
                state = state.enableSecurityMode,
                onSwitchClick = {
                    viewModel.sendEvent(
                        MoreScreenEvent.SecurityModeChanged(
                            !state.enableSecurityMode
                        )
                    )
                }
            ) { old, new ->
                FlowBus.with<ToRouteEvent>()
                    .post(rememberCoroutineScope, ToRouteEvent("security"))
            }
        }
        item {
            TextPreferenceComponent(title = "书架", icon = Icons.Default.Category) {
                FlowBus.with<NavigationEvent>()
                    .post(
                        rememberCoroutineScope,
                        NavigationEvent(NavigationRoute.CategoryScreen.route)
                    )
            }
        }
        item {
            TextPreferenceComponent(title = "设置", icon = Icons.Default.Settings) {
                FlowBus.with<NavigationEvent>()
                    .post(
                        rememberCoroutineScope,
                        NavigationEvent(NavigationRoute.SettingsScreen.route)
                    )
            }
        }
    }
}