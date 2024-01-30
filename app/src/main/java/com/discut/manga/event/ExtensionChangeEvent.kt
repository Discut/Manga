package com.discut.manga.event

import com.discut.core.flowbus.BusEvent

sealed class ExtensionChangeEvent(
    val packageName: String
) : BusEvent {
    class Added(packageName: String) : ExtensionChangeEvent(packageName)
    class Removed(packageName: String) : ExtensionChangeEvent(packageName)
    class Replace(packageName: String) : ExtensionChangeEvent(packageName)
}