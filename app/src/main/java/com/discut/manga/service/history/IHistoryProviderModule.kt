package com.discut.manga.service.history

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IHistoryProviderModule {
    @Binds
    abstract fun bindIHistoryProvider(historyProvider: HistoryProviderImpl): IHistoryProvider
}