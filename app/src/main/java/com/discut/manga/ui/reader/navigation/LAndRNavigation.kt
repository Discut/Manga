package com.discut.manga.ui.reader.navigation

import android.graphics.RectF

class LAndRNavigation : BaseReaderClickNavigation() {
    override fun buildClickRegionList(): List<ClickRegion> = listOf(
        ClickRegion(
            region = RectF(0f, 0f, 0.33f, 1f),
            type = NavigationRegion.LEFT
        ),
        ClickRegion(
            region = RectF(0.66f, 0f, 1f, 1f),
            type = NavigationRegion.RIGHT
        )
    )
}