package com.discut.manga.ui.reader.navigation

class NoneNavigation : BaseReaderClickNavigation() {
    override fun buildClickRegionList(): List<ClickRegion> = emptyList()
}