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
        val doNotHaveLock =
            redisClient.getBucket<String>(redisKey).setIfAbsent(lockValue, lockAtMostFor.toJavaDuration()).not()

        if (doNotHaveLock) throw AlreadyLockedException()

        block.invoke().apply {
            if (resetLockUntilAfterComplete) redisClient.getBucket<String>(redisKey).delete()
        }
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
        val doNotHaveLock = redisClient.getBucket<String>(redisKey).setIfAbsent(lockValue, lockAtMostFor).not()

        if (doNotHaveLock) throw AlreadyLockedException()

        block.invoke().apply {
            if (resetLockUntilAfterComplete) redisClient.getBucket<String>(redisKey).delete()
        }
    }
}

private const val SHEDLOCK_PREFIX = "shedlock:"