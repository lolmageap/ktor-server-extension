package extension.ktor.redis

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

suspend fun <T> distributedLock(
    name: String,
    waitTime: kotlin.time.Duration,
    leaseTime: kotlin.time.Duration = 1.seconds,
    block: suspend () -> T,
): T {
    val redisKey = DISTRIBUTED_LOCK_PREFIX + name

    return withContext(IO) {
        val redisClient = RedissonClientHolder.redissonClient
        val lock = redisClient.getLock(redisKey)

        val waitTimeToLong = waitTime.toJavaDuration().toMillis()
        val leaseTimeToLong = leaseTime.toJavaDuration().toMillis()

        try {
            val hasLock =
                lock.tryLockAsync(waitTimeToLong, leaseTimeToLong, MILLISECONDS).get()

            if (hasLock) block.invoke()
            else throw AlreadyLockedException()
        } finally {
            lock.unlockAsync()
        }
    }
}

suspend fun <T> distributedLock(
    name: String,
    waitTime: java.time.Duration,
    leaseTime: java.time.Duration,
    block: suspend () -> T,
): T {
    val redisKey = DISTRIBUTED_LOCK_PREFIX + name

    return withContext(IO) {
        val redisClient = RedissonClientHolder.redissonClient
        val lock = redisClient.getLock(redisKey)

        val waitTimeToLong = waitTime.toMillis()
        val leaseTimeToLong = leaseTime.toMillis()

        try {
            val hasLock =
                lock.tryLockAsync(waitTimeToLong, leaseTimeToLong, MILLISECONDS).get()

            if (hasLock) block.invoke()
            else throw AlreadyLockedException()
        } finally {
            lock.unlockAsync()
        }
    }
}

private const val DISTRIBUTED_LOCK_PREFIX = "distributed-lock:"