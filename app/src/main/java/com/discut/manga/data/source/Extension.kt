package com.discut.manga.data.source

import android.graphics.drawable.Drawable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import manga.core.network.ProgressListener
import manga.source.Source

@Serializable
sealed class Extension {
    abstract val name: String
    abstract val pkg: String
    abstract val version: String
    abstract val versionCode: Long

    sealed class LocalExtension : Extension() {

        abstract val pkgFactory: String?
        abstract val sources: List<Source>
        abstract val icon: Drawable?

        data class Success(
            override val name: String,
            override val pkg: String,
            override val version: String,
            override val versionCode: Long,
            override val pkgFactory: String?,
            override val sources: List<Source>,
            override val icon: Drawable?,
        ) : LocalExtension()

        data class Error(
            override val name: String,
            override val pkg: String,
            override val version: String,
            override val versionCode: Long,
            override val pkgFactory: String?,
            override val sources: List<Source>,
            override val icon: Drawable?,
            val error: Throwable,
            val msg: String?,
        ) : LocalExtension()
    }

    @Serializable
    data class RemoteExtension(
        override val name: String,
        override val pkg: String,
        override val version: String,
        override val versionCode: Long,
        val apkUrl: String,
        val iconUrl: String,
        val sources: List<RemoteSource>
    ) : Extension(), ProgressListener {

        @Transient
        private val _state = MutableStateFlow(State.WAITING)


        @Transient
        val state = _state.asStateFlow()
        override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
            if (done) {
                _state.update { State.DOWNLOADED }
            } else {
                _state.update { State.DOWNLOADING }
            }
        }

        fun onInstall() {
            _state.update { State.INSTALLING }
        }

        fun onInstalled() {
            _state.update { State.INSTALLED }
        }

        fun onCanceled() {
            _state.update { State.WAITING }
        }


        enum class State {
            WAITING,
            DOWNLOADING,
            DOWNLOADED,
            INSTALLING,
            INSTALLED, // When the extension is installed, it will be removed from the list, so it will not be displayed, we don't have to deal it.
            ERROR // Same as above
        }

    }
}
