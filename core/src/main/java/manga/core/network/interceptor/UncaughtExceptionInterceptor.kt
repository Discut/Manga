package manga.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * UncaughtExceptionInterceptor
 * Catches any uncaught exceptions from later in the chain and rethrows as a non-fatal
 * IOException to avoid catastrophic failure.
 *
 * This should be the first interceptor in the client.
 */
class UncaughtExceptionInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request())
        } catch (e: Exception) {
            throw NetworkException(e)
        }
    }
}

class NetworkException(e: Exception) : IOException(e)