package com.discut.manga.ui.reader.navigation

import android.graphics.RectF

class LShapeNavigation : BaseReaderClickNavigation() {
    override fun buildClickRegionList(): List<ClickRegion> = listOf(
        ClickRegion(
            region = RectF(0f, 0f, 0.66f, 0.33f),
            type = NavigationRegion.PREV,
        ),
        ClickRegion(
            region = RectF(0f, 0.33f, 0.33f, 0.66f),
            type = NavigationRegion.PREV,
        ),
        ClickRegion(
            region = RectF(0f, 0.66f, 1f, 1f),
            type = NavigationRegion.NEXT,
        ),
        ClickRegion(
            region = RectF(0.66f, 0.33f, 1f, 0.66f),
            type = NavigationRegion.NEXT,
        ),
    )
}