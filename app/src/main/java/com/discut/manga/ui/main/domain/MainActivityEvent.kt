package com.discut.manga.ui.main.domain

import com.discut.core.flowbus.BusEvent

data class ToRouteEvent(val route: String) : BusEvent
