package manga.source.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import manga.core.network.ProgressListener

@Serializable
open class Page(
    val index: Int,
    val url: String = "",
    var imageUrl: String? = null,
    @Transient
    private val defaultStatus: State = State.QUEUE,
) : ProgressListener {

    val number: Int
        get() = index + 1

    @Transient
    private val _statusFlow: MutableStateFlow<State> = MutableStateFlow(defaultStatus)

    @Transient
    val statusFlow = _statusFlow.asStateFlow()
    var status: State
        get() = _statusFlow.value
        set(value) {
            _statusFlow.update {
                value
            }
        }

    @Transient
    private val _progressFlow = MutableStateFlow(0)

    @Transient
    val progressFlow = _progressFlow.asStateFlow()

    var progress: Int
        get() = _progressFlow.value
        set(value) {
            _progressFlow.value = value
        }

    enum class State {
        QUEUE,// queue
        LOAD_PAGE,// loading page info
        DOWNLOAD_IMAGE,// downloading image
        READY, // image downloaded
        ERROR,// error
    }

    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
        progress = if (contentLength > 0) {
            (100 * bytesRead / contentLength).toInt()
        } else {
            -1
        }
    }
}
