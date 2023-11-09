package com.discut.manga.ui.main

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.discut.core.mvi.CollectSideEffect
import com.discut.manga.ui.main.domain.MainEffect
import com.discut.manga.ui.main.domain.MainEvent
import com.discut.manga.ui.main.domain.MainState
import com.discut.manga.ui.main.domain.NavBarItem

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    viewModel.CollectSideEffect {
        when (it) {
            is MainEffect.NavigateTo -> navController.navigate(it.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

            is MainEffect.OpenAbout -> navController.navigate("about")
        }
    }
    HomeScreenContent(
        state = state,
        navBarIsSelected = { item ->
            navController.currentBackStackEntryAsState().value?.destination?.route == item.route
        },
        navBarClick = { viewModel.sendEvent(MainEvent.ClickNavigationItem(it)) },
    ) {
        NavHost(
            navController = navController,
            startDestination = MainViewModel.DEFAULT_SCREEN_ROUTE,
            modifier = Modifier.padding(it)
        ) {
            state.navBarItems.forEach { navBarItem ->
                composable(navBarItem.route) {
                    navBarItem.screen.invoke()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    state: MainState,
    navBarIsSelected: @Composable (item: NavBarItem) -> Boolean,
    navBarClick: (item: NavBarItem) -> Unit,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.wrapContentHeight()) {
                state.navBarItems.forEach {
                    NavigationBarItem(
                        icon = { Icon(imageVector = it.icon, contentDescription = null) },
                        label = { Text(text = it.title) },
                        selected = navBarIsSelected(it),
                        onClick = { navBarClick(it) }
                    )
                }
            }
        }) {
        content(it)
        BackHandler {
            context.startActivity(Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
}