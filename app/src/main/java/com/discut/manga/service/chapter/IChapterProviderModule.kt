package com.discut.manga.service.chapter

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IChapterProviderModule {

    @Binds
    abstract fun bindIChapterProvider(chapterProvider: ChapterProviderImpl): IChapterProvider
}