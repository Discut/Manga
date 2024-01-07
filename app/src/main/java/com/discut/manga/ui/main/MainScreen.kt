package com.discut.manga.ui.main

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.discut.core.flowbus.observeEvent
import com.discut.core.mvi.CollectSideEffect
import com.discut.manga.event.GlobalAttentionToastEvent
import com.discut.manga.theme.padding
import com.discut.manga.ui.main.domain.MainEffect
import com.discut.manga.ui.main.domain.MainEvent
import com.discut.manga.ui.main.domain.MainState
import com.discut.manga.ui.main.domain.NavBarItem
import com.discut.manga.util.launchIO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            state.navBarItems.forEach { navBarItem ->
                composable(
                    route = navBarItem.route,
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                ) {
                    navBarItem.screen.invoke()
                }
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    state: MainState,
    navBarIsSelected: @Composable (item: NavBarItem) -> Boolean,
    navBarClick: (item: NavBarItem) -> Unit,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val context = LocalContext.current
    var msgFlow by remember {
        mutableStateOf<GlobalAttentionToastEvent?>(null)
    }
    var isShow by remember {
        mutableStateOf(false)
    }
    var attentionToastJob: Job? = null
    LocalLifecycleOwner.current.observeEvent<GlobalAttentionToastEvent> {
        msgFlow = it
    }
    Scaffold(
        topBar = {
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.wrapContentHeight()) {
                state.navBarItems.filter { !it.hide }.forEach {
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
        LaunchedEffect(key1 = msgFlow) {
            if (msgFlow == null) {
                return@LaunchedEffect
            }
            if (isShow) {
                isShow = false
                attentionToastJob?.cancel()
                delay(500)
            }
            isShow = true
            attentionToastJob = launchIO {
                delay(msgFlow?.duration?.toLong() ?: 2000)
                isShow = false
            }
        }
        AnimatedVisibility(
            visible = isShow,
            enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = msgFlow?.type?.color ?: MaterialTheme.colorScheme.primaryContainer
                    )
                    .fillMaxWidth()
                    .padding(top = it.calculateTopPadding(), bottom = MaterialTheme.padding.Normal),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = msgFlow?.msg ?: "")
            }
        }
        BackHandler {
            context.startActivity(Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
}