package com.discut.manga.ui.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discut.core.flowbus.FlowBus
import com.discut.manga.components.preference.SwitchPreferenceComponent
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.event.GlobalAttentionToastEvent
import com.discut.manga.event.GlobalAttentionToastType
import com.discut.manga.event.NavigationEvent
import com.discut.manga.navigation.NavigationRoute
import com.discut.manga.theme.padding
import com.discut.manga.ui.main.domain.ToRouteEvent

@Composable
fun MoreScreen() {
    val viewModel: MoreScreenViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val rememberCoroutineScope = rememberCoroutineScope()

    LazyColumn {

        item {
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.padding.Large,
                        vertical = MaterialTheme.padding.ExtraLarge
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Manga", style = MaterialTheme.typography.headlineLarge)
                Text(
                    text = "V1.0",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        item {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
        item {
            SwitchPreferenceComponent(
                title = "隐私模式", subTitle = "数据更安全",
                icon = Icons.Default.Security,
                state = state.enableSecurityMode,
                onSwitchClick = {
                    viewModel.sendEvent(
                        MoreScreenEvent.SecurityModeChanged(
                            !state.enableSecurityMode
                        )
                    )
                }
            ) { _, _ ->
                FlowBus.with<ToRouteEvent>()
                    .post(rememberCoroutineScope, ToRouteEvent("security"))
            }
        }
        item {
            SwitchPreferenceComponent(
                title = "无痕模式", subTitle = "关闭历史记录",
                icon = Icons.Default.RemoveRedEye,
                state = state.enableNoTranceMode,
            ) { _, new ->
                FlowBus.with<GlobalAttentionToastEvent>()
                    .post(
                        rememberCoroutineScope,
                        GlobalAttentionToastEvent(
                            "无痕模式${if (new) "开启" else "关闭"}",
                            2000,
                            GlobalAttentionToastType.Normal
                        )
                    )
                viewModel.sendEvent {
                    MoreScreenEvent.NoTranceModeChanged(new)
                }
            }
        }
        item {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
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