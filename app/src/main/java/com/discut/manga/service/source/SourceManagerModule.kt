package com.discut.manga.service.source

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SourceManagerModule {
    @Binds
    abstract fun bindISourceManager(sourceManager: SourceManager): ISourceManager
}