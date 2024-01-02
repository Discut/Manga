package com.discut.manga.source

import managa.source.Source


fun Source.isLocal(): Boolean {
    return id == 0L
}