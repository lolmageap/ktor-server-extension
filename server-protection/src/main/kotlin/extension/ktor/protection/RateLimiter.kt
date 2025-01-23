package extension.ktor.protection

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.ZonedDateTime
import kotlin.time.toKotlinDuration

private val rateLimiterMap = mutableMapOf<String, RateLimiter>()
private val mutex = Mutex()

private data class RateLimiter(
    val startUpTime: ZonedDateTime,
    val count: Long,
)

suspend fun <T> rateLimiter(
    key: String,
    limit: Long,
    period: kotlin.time.Duration,
    block: suspend () -> T,
) {
    val now = ZonedDateTime.now()

    mutex.withLock(RATE_LIMITER_PREFIX + key) {
        if (rateLimiterMap hasNot key) rateLimiterMap[key] = RateLimiter(now, 1)

        val rateLimiter = rateLimiterMap.getValue(key)

        val isExpired = now - rateLimiter.startUpTime > period.inWholeMilliseconds
        if (isExpired) rateLimiterMap[key] = RateLimiter(now, 1)

        if (rateLimiter.count >= limit) {
            rateLimiterMap[key] = rateLimiter.copy(count = rateLimiter.count + 1)
            throw RateLimitExceededException()
        }
    }

    block()
}

suspend fun <T> rateLimiter(
    key: String,
    limit: Long,
    period: java.time.Duration,
    block: suspend () -> T,
) {
    val now = ZonedDateTime.now()

    mutex.withLock(RATE_LIMITER_PREFIX + key) {
        if (rateLimiterMap hasNot key) rateLimiterMap[key] = RateLimiter(now, 1)

        val rateLimiter = rateLimiterMap.getValue(key)

        val isExpired = now - rateLimiter.startUpTime > period.toKotlinDuration().inWholeMilliseconds
        if (isExpired) rateLimiterMap[key] = RateLimiter(now, 1)

        if (rateLimiter.count >= limit) {
            rateLimiterMap[key] = rateLimiter.copy(count = rateLimiter.count + 1)
            throw RateLimitExceededException()
        }
    }

    block()
}

private const val RATE_LIMITER_PREFIX = "rate_limiter:"

private infix fun <T, R> Map<T, R>.hasNot(
    key: T,
) = !containsKey(key)

private operator fun ZonedDateTime.minus(
    time: ZonedDateTime,
) = this.toInstant().toEpochMilli() - time.toInstant().toEpochMilli()