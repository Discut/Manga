package com.discut.manga.service.source

import manga.source.Source


fun Source.isLocal(): Boolean {
    return id == 0L
}