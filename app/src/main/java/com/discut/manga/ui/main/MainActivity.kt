package com.discut.manga.ui.main

import android.os.Bundle
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.discut.core.flowbus.observeEvent
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.main.domain.ToRouteEvent
import com.discut.manga.ui.util.GlobalNavigationRoute
import com.discut.manga.ui.util.graph
import com.discut.manga.util.setComposeContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setComposeContent {
            val navController = rememberNavController()
            LocalLifecycleOwner.current.observeEvent<ToRouteEvent> {
                navController.navigate(it.route){
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            NavHost(navController = navController, startDestination = "/main") {
                GlobalNavigationRoute.graph().forEach { (r, s) ->
                    composable(route = r) {
                        s.invoke()
                    }
                }
            }
        }
    }
}