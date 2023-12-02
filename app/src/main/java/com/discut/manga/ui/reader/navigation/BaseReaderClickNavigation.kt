package com.discut.manga.ui.reader.navigation

import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.StringRes
import discut.manga.common.res.R

abstract class BaseReaderClickNavigation {

    sealed class NavigationRegion(@StringRes val nameRes: Int, val color: Int) {
        data object MENU :
            NavigationRegion(R.string.reader_click_nav_menu, Color.argb(0xCC, 0x95, 0x81, 0x8D))

        data object PREV :
            NavigationRegion(R.string.reader_click_nav_prev, Color.argb(0xCC, 0xFF, 0x77, 0x33))

        data object NEXT :
            NavigationRegion(R.string.reader_click_nav_next, Color.argb(0xCC, 0x84, 0xE2, 0x96))

        data object LEFT :
            NavigationRegion(R.string.reader_click_nav_left, Color.argb(0xCC, 0x7D, 0x11, 0x28))

        data object RIGHT :
            NavigationRegion(R.string.reader_click_nav_right, Color.argb(0xCC, 0xA6, 0xCF, 0xD5))
    }

    protected val regionList: List<ClickRegion> = listOf(
        ClickRegion(
            region = RectF(0f, 0f, 1f, 1f),
            type = NavigationRegion.MENU
        )
    ) + buildClickRegionList()

    protected abstract fun buildClickRegionList(): List<ClickRegion>

    data class ClickRegion(
        val region: RectF,
        val type: NavigationRegion
    )

    fun getAction(position: PointF): NavigationRegion {
        return regionList.last { it.region.contains(position.x, position.y) }
            .type
    }
}