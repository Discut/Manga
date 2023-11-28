package com.discut.manga.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import discut.manga.data.MangaAppDatabase

@Module
@InstallIn(ViewModelComponent::class)
object DataBaseModule {
    @Provides
    fun provideDatabaseManager(): MangaAppDatabase {
        return MangaAppDatabase.DB
    }
}