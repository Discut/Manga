package com.discut.manga.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "设置",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        SettingsScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            onSecuritySettingsClick = { navController.navigate("security") },
            onDownloadSettingsClick = { navController.navigate(NavigationRoute.SettingsScreen.Download.route) },
            onBrowseSettingsClick = { navController.navigate(NavigationRoute.SettingsScreen.Browse.route) }
        )
    }
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    onSecuritySettingsClick: () -> Unit,
    onBrowseSettingsClick: () -> Unit,
    onDownloadSettingsClick: () -> Unit
) {
    Column(modifier = modifier) {
        TextPreferenceComponent(
            title = "隐私",
            subTitle = "app锁定、隐藏预览图",
            icon = Icons.Default.Security,
            onPreferenceClick = onSecuritySettingsClick
        )
        TextPreferenceComponent(
            title = "浏览",
            subTitle = "添加仓库",
            icon = Icons.Outlined.Explore,
            onPreferenceClick = onBrowseSettingsClick
        )
        TextPreferenceComponent(
            title = "下载",
            subTitle = "自动下载、下载路径",
            icon = Icons.Outlined.Download,
            onPreferenceClick = onDownloadSettingsClick
        )
    }
}