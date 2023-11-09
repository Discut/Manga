package com.discut.manga.ui.settings.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discut.manga.components.preference.HighlightSwitchPreferenceComponent
import com.discut.manga.components.preference.TipsPreferenceComponent
import com.discut.manga.ui.settings.security.domain.SecuritySettingsEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen() {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val vm: SecuritySettingsViewModel = viewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        //modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                /*                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),*/
                title = {
                    Text(
                        "Security Mode",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                /*actions = {
                    IconButton(onClick = { *//* do something *//* }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },*/
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        SecuritySettingsScreenContent(modifier = Modifier
            .padding(innerPadding)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
            checkSecurityMode = state.enableSecurityMode,
            onSecurityModeCheckedChange = {
                vm.sendEvent(
                    SecuritySettingsEvent.ClickSecurityModeComponent(it)
                )
            })
    }
}

@Composable
private fun SecuritySettingsScreenContent(
    modifier: Modifier = Modifier,
    checkSecurityMode: Boolean = false,
    onSecurityModeCheckedChange: ((Boolean) -> Unit)
) {
    Column(modifier = modifier) {
        HighlightSwitchPreferenceComponent(
            title = "Enable Security Mode",
            checked = checkSecurityMode,
            onCheckedChange = onSecurityModeCheckedChange
        )
        TipsPreferenceComponent(text = "锁定app，阻止系统截屏，并在切换至后台时隐藏预览图。")
    }

}