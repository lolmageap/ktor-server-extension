package extension.ktor.redis

import org.redisson.api.RRateLimiter
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

    if (rateLimiter.isNotExists) rateLimiter.trySetRate(RateType.OVERALL, limit, period.toJavaDuration())
    if (rateLimiter.notAcquired) throw RateLimitExceededException()

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

    if (rateLimiter.isNotExists) rateLimiter.trySetRate(RateType.OVERALL, limit, period)
    if (rateLimiter.notAcquired) throw RateLimitExceededException()

    return block()
}

data class RateLimitExceededException(
    override val message: String = "Rate limit exceeded",
) : RuntimeException(message)

val RRateLimiter.isNotExists: Boolean
    get() = isExists.not()

val RRateLimiter.notAcquired: Boolean
    get() = tryAcquire().not()