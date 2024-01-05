package managa.source.extensions

import okhttp3.Response
import okio.buffer
import okio.sink
import java.io.InputStream
import java.io.OutputStream

@Suppress("Unused")
fun Response.transferTo(outputStream: OutputStream) {
    body.source().use { source ->
        outputStream.sink().buffer().use {
            it.writeAll(source)
            it.flush()
        }
    }
}

@Suppress("Unused")
fun Response.toInputStream(): InputStream = body.source().inputStream()