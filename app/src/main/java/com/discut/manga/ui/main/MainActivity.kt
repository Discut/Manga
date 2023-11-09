package com.discut.manga.ui.main

import android.os.Bundle
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.discut.core.flowbus.observeEvent
import com.discut.manga.navigation.NavigationEvent
import com.discut.manga.navigation.NavigationRoute
import com.discut.manga.navigation.settingsGraph
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.main.domain.ToRouteEvent
import com.discut.manga.util.setComposeContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setComposeContent {
            val navController = rememberNavController()
            LocalLifecycleOwner.current.observeEvent<ToRouteEvent> {
                navController.navigate(it.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            LocalLifecycleOwner.current.observeEvent<NavigationEvent> {
                navController.navigate(it.route)
            }
            NavHost(
                navController = navController,
                startDestination = NavigationRoute.MainScreen.route
            ) {
                composable(NavigationRoute.MainScreen.route) {
                    MainScreen()
                }
                settingsGraph(navController)
            }
        }
    }
}