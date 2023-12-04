package com.discut.manga.util

import com.discut.core.flowbus.FlowBus
import com.discut.manga.navigation.NavigationEvent
import kotlinx.coroutines.CoroutineScope

fun NavigationEvent.postBy(scope: CoroutineScope) {
    FlowBus.with<NavigationEvent>()
        .post(
            scope,
            NavigationEvent(route)
        )
}