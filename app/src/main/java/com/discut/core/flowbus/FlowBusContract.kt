package com.discut.core.flowbus

interface BusEvent

data class MainScreenToastEvent(val msg: String) : BusEvent
