package com.discut.manga.data.category

import discut.manga.data.category.Category

val DefaultCategory = Category(
    id = Category.UNCATEGORIZED_ID,
    name = "Default",
    order = -1
)

val DefaultCategories = listOf(DefaultCategory)