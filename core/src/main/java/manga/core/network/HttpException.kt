package manga.core.network

class HttpException(val code: Int) : IllegalStateException("HTTP error $code")
