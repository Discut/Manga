package com.discut.manga.source

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SourceManagerInterface {
    fun getInstance(): SourceManager
}