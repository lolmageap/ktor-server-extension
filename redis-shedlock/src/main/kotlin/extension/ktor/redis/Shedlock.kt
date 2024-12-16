package extension.ktor.redis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import kotlin.time.toJavaDuration

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: kotlin.time.Duration,
    resetLockUntilAfterComplete: Boolean = true,
    block: suspend () -> T,
) {
    val redisKey = SHEDLOCK_PREFIX + name
    val lockValue = System.currentTimeMillis().toString()

    return withContext(Dispatchers.IO) {
        val redisClient = RedissonClientHolder.redissonClient
        val hasLock =
            redisClient.getBucket<String>(redisKey).setIfAbsent(lockValue, lockAtMostFor.toJavaDuration())

        if (hasLock) {
            block.invoke().apply {
                if (resetLockUntilAfterComplete) redisClient.getBucket<String>(redisKey).delete()
            }
        } else throw AlreadyLockedException()
    }
}

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: Duration,
    resetLockUntilAfterComplete: Boolean = true,
    block: suspend () -> T
): T {
    val redisKey = SHEDLOCK_PREFIX + name
    val lockValue = System.currentTimeMillis().toString()

    return withContext(Dispatchers.IO) {
        val redisClient = RedissonClientHolder.redissonClient
        val hasLock = redisClient.getBucket<String>(redisKey).setIfAbsent(lockValue, lockAtMostFor)

        if (hasLock) {
            block.invoke().apply {
                if (resetLockUntilAfterComplete) redisClient.getBucket<String>(redisKey).delete()
            }
        } else throw AlreadyLockedException()
    }
}

private const val SHEDLOCK_PREFIX = "shedlock:"