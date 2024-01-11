package com.discut.manga.service.saver.download.model

import com.discut.core.flowbus.BusEvent

sealed interface DownloadEvent : BusEvent {
    data class DownloadScopeStop(val scopeKey: String) : DownloadEvent
}