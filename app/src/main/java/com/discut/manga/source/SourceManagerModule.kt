package com.discut.manga.source

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class SourceManagerModule {
    @Binds
    abstract fun bindISourceManager(sourceManager: SourceManager): ISourceManager
}