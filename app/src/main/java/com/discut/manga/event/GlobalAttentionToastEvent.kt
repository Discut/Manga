package com.discut.manga.event

import androidx.compose.ui.graphics.Color
import com.discut.core.flowbus.BusEvent

data class GlobalAttentionToastEvent(
    val msg: String,
    val duration: Int,
    val type: GlobalAttentionToastType
) : BusEvent

sealed class GlobalAttentionToastType(
    val color: Color?
) {
    data object Normal : GlobalAttentionToastType(null)
    data object Error : GlobalAttentionToastType(Color.Red)
    data object Success : GlobalAttentionToastType(Color.Green)

}