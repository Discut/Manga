package com.discut.manga.service.source

import managa.source.Source


fun Source.isLocal(): Boolean {
    return id == 0L
}