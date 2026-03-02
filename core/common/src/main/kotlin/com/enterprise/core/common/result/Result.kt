package com.enterprise.core.common.result

/**
 * Sealed result type used across all layers.
 *
 * Replaces raw exceptions crossing layer boundaries.
 * Domain layer returns Result<T>; UI maps to UiState.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: AppException) : Result<Nothing>
    data object Loading : Result<Nothing>
}

/** Typed exception hierarchy. Prevents catching raw Throwable in UI. */
sealed class AppException(message: String, cause: Throwable? = null) :
    Exception(message, cause) {

    class NetworkException(message: String, cause: Throwable? = null) :
        AppException(message, cause)

    class ServerException(val code: Int, message: String) :
        AppException(message)

    class UnauthorizedException : AppException("Session expired")

    class NotFoundException(resource: String) :
        AppException("Not found: $resource")

    class UnknownException(cause: Throwable) :
        AppException("Unknown error: ${cause.message}", cause)
}

/** Maps any Throwable to AppException. Use in repository catch blocks. */
fun Throwable.toAppException(): AppException = when (this) {
    is AppException -> this
    is java.net.UnknownHostException -> AppException.NetworkException("No internet connection", this)
    is java.net.SocketTimeoutException -> AppException.NetworkException("Connection timed out", this)
    else -> AppException.UnknownException(this)
}

/** Inline map for Result.Success. */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error   -> this
    is Result.Loading -> this
}

/** Executes block and wraps in Result, mapping exceptions automatically. */
suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Result<T> =
    try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e.toAppException())
    }