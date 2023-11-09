package com.discut.manga.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.discut.manga.ui.settings.SettingsScreen
import com.discut.manga.ui.settings.security.SecuritySettingsScreen

sealed class NavigationRoute(val route: String) {
    data object MainScreen : NavigationRoute("main")

    data object SettingsScreen : NavigationRoute("settings") {
        data object Security : NavigationRoute("security")
        data object SettingsMain : NavigationRoute("settingsMain")
    }
}

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    navigation(
        startDestination = NavigationRoute.SettingsScreen.SettingsMain.route,
        route = NavigationRoute.SettingsScreen.route
    ) {
        composable(route = NavigationRoute.SettingsScreen.SettingsMain.route,
            enterTransition = {
                scaleIntoContainer()
            },
            exitTransition = {
                scaleOutOfContainer(direction = ScaleTransitionDirection.INWARDS)
            },
            popEnterTransition = {
                scaleIntoContainer(direction = ScaleTransitionDirection.OUTWARDS)
            },
            popExitTransition = {
                scaleOutOfContainer()
            }) {
            SettingsScreen(navController)
        }
        composable(NavigationRoute.SettingsScreen.Security.route) {
            SecuritySettingsScreen()
        }
    }
}

/**
 * 动画文章 https://juejin.cn/post/7046855025758306317#heading-12
 */
fun scaleIntoContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
    initialScale: Float = if (direction != ScaleTransitionDirection.OUTWARDS) 0.95f else 1.05f
): EnterTransition {
    return scaleIn(
        animationSpec = tween(180, delayMillis = 90),
        initialScale = initialScale
    ) + fadeIn(animationSpec = tween(180, delayMillis = 90))
}


enum class ScaleTransitionDirection {
    OUTWARDS, INWARDS
}

fun scaleOutOfContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
    targetScale: Float = if (direction != ScaleTransitionDirection.INWARDS) 0.95f else 1.05f
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = 180,
            delayMillis = 90
        ), targetScale = targetScale
    ) + fadeOut(tween(delayMillis = 90))
}