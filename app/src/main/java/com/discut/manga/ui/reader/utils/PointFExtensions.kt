package com.discut.manga.ui.reader.utils

import android.graphics.PointF
import com.discut.manga.ui.reader.navigation.BaseReaderClickNavigation

fun PointF.transformToAction(navigation: BaseReaderClickNavigation): BaseReaderClickNavigation.NavigationRegion {
    return navigation.getAction(this)
}