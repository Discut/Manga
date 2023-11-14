package manga.core.network

import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

fun Call.asFlow(): Flow<Response> = callbackFlow {
    enqueue(object : okhttp3.Callback {
        override fun onFailure(call: Call, e: IOException) {
            call.cancel()
            close(e)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                trySendBlocking(response).onFailure {
                    call.cancel()
                    close()
                }
                close()
            } else {
                cancel("bad response")
            }
        }
    })
    awaitClose {
        cancel()
    }
}