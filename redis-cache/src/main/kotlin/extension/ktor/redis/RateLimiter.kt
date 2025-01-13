package extension.ktor.redis

import org.redisson.api.RateType
import kotlin.time.toJavaDuration

suspend fun <T> rateLimiter(
    key: String,
    limit: Long,
    period: kotlin.time.Duration,
    block: suspend () -> T,
): T {
    val redisClient = RedissonClientHolder.redissonClient

    val rateLimiter = redisClient.getRateLimiter(key)
    val isRateLimited = rateLimiter.trySetRate(RateType.OVERALL, limit, period.toJavaDuration())

    if (isRateLimited) throw RateLimitExceededException()
    return block()
}

suspend fun <T> rateLimiter(
    key: String,
    limit: Long,
    period: java.time.Duration,
    block: suspend () -> T,
): T {
    val redisClient = RedissonClientHolder.redissonClient

    val rateLimiter = redisClient.getRateLimiter(key)
    val isRateLimited = rateLimiter.trySetRate(RateType.OVERALL, limit, period)

    if (isRateLimited) throw RateLimitExceededException()
    return block()
}

data class RateLimitExceededException(
    override val message: String = "Rate limit exceeded",
) : RuntimeException(message)