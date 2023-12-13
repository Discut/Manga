package com.discut.manga.ui.categories

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import discut.manga.data.category.Category

data class CategoryState(
    internal val categories: List<Category> = emptyList(),
) : UiState

sealed interface CategoryEvent : UiEvent {
    data class ItemMove(val from: Int, val to: Int) : CategoryEvent

    data class EditedCategory(val category: Category) : CategoryEvent

    data class AddNewCategory(val category: String) : CategoryEvent

    data class DeleteCategory(val category: Category) : CategoryEvent
}

sealed interface CategoryEffect : UiEffect

