package com.discut.manga.source

import managa.source.Source

interface ISourceManager {
    fun get(sourceKey: Long): Source?
}