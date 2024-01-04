package com.discut.manga.service.manga

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IMangaProviderModule {

    @Binds
    abstract fun bindIMangaProvider(mangaProvider: MangaProviderImpl): IMangaProvider
}