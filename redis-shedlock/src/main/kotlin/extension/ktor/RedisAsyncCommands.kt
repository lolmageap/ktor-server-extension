package extension.ktor

import io.lettuce.core.RedisFuture
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun <T> RedisFuture<T>.await() =
    suspendCoroutine<T> { continuation ->
        this.whenComplete { result, exception ->
            if (exception != null) continuation.resumeWith(Result.failure(exception))
            else continuation.resume(result)
        }
    }