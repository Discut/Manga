package com.discut.manga.event

import com.discut.core.flowbus.BusEvent

data class NavigationEvent(val route: String) : BusEvent