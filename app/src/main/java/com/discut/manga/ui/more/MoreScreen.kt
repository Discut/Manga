package com.discut.manga.ui.more

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discut.core.flowbus.FlowBus
import com.discut.core.mvi.CollectSideEffect
import com.discut.manga.components.preference.SwitchPreferenceComponent
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
                        MoreScreenEvent.ClickSecurityMode(
                            !state.enableSecurityMode
                        )
                    )
                }
            ) { old, new ->
                FlowBus.with<ToRouteEvent>().post(rememberCoroutineScope, ToRouteEvent("/settings/security"))
            }
        }
    }
}