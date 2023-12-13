package com.discut.manga.ui.categories

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.util.launchIO
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.category.Category
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val db: MangaAppDatabase
) :
    BaseViewModel<CategoryState, CategoryEvent, CategoryEffect>() {

    init {
        launchIO {
            val categories = fetchCategories()
            sendState {
                copy(
                    categories = categories
                )
            }
        }
    }

    private suspend fun fetchCategories(): List<Category> =
        withIOContext {
            db.categoryDao().getAll().sortedBy { it.order }
        }


    override fun initialState(): CategoryState = CategoryState()

    override suspend fun handleEvent(event: CategoryEvent, state: CategoryState): CategoryState {
        return when (event) {
            is CategoryEvent.ItemMove -> {
                val toMutableList = state.categories.toMutableList()
                val from = toMutableList[event.from]
                val to = toMutableList[event.to]
                toMutableList[event.from] = to
                toMutableList[event.to] = from

                val order = from.order
                withIOContext {
                    db.categoryDao().update(
                        from.copy(
                            order = to.order
                        )
                    )
                    db.categoryDao().update(
                        to.copy(
                            order = order
                        )
                    )
                }

                state.copy(
                    categories = toMutableList.toList()
                )
            }

            is CategoryEvent.EditedCategory -> {
                withIOContext {
                    db.categoryDao().update(event.category)
                    val all = fetchCategories()
                    state.copy(
                        categories = all
                    )
                }
            }

            is CategoryEvent.AddNewCategory -> {
                val order =
                    state.categories.getOrNull(state.categories.lastIndex)?.order?.plus(1) ?: 0
                val id = SnowFlakeUtil.generateSnowFlake()
                withIOContext {
                    state.copy(
                        categories = db.categoryDao().run {
                            insert(
                                Category(
                                    id = id,
                                    name = event.category,
                                    order = order
                                )
                            )
                            fetchCategories()
                        }
                    )
                }
            }

            is CategoryEvent.DeleteCategory -> {
                withIOContext {
                    db.categoryDao().delete(event.category)
                    state.copy(
                        categories = state.categories - event.category
                    )
                }
            }

        }
    }
}