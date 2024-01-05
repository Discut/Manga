package com.discut.manga.service.source

import managa.source.Source

interface ISourceManager {
    fun get(sourceKey: Long): Source?

    fun getAll(): List<Source>
}