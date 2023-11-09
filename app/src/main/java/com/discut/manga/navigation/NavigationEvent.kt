package com.discut.manga.navigation

import com.discut.core.flowbus.BusEvent

data class NavigationEvent(val route: String) : BusEvent