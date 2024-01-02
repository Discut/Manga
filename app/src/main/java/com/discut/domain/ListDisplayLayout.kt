package com.discut.domain

sealed interface ListDisplayLayout {
    data object List : ListDisplayLayout
    data object LooseGrid : ListDisplayLayout
}