package com.discut.manga.data.manga

data class UpdateManga(
    val id: Long,
    var source: Long? = null,
    var favorite: Boolean? = null,
    var lastUpdate: Long? = null,
    var nextUpdate: Long? = null,
    var fetchInterval: Int? = null,
    var dateAdded: Long? = null,
    var viewerFlags: Long? = null,
    var chapterFlags: Long? = null,
    var coverLastModified: Long? = null,
    var url: String? = null,
    var title: String? = null,
    var artist: String? = null,
    var author: String? = null,
    var description: String? = null,
    var genre: List<String>? = null,
    var status: Long? = null,
    var thumbnailUrl: String? = null,
    //val updateStrategy: UpdateStrategy,
    var category: Long? = null,
    var initialized: Boolean? = null,
)