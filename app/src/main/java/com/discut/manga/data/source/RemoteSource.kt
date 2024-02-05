package com.discut.manga.data.source

import kotlinx.serialization.Serializable

@Serializable
data class RemoteSource(
    val name: String,
    val id: Long,
    val baseUrl: String,
)