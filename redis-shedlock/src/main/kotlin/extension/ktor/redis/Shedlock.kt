package extension.ktor.redis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import kotlin.time.toJavaDuration

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: kotlin.time.Duration,
    block: suspend () -> T,
) {
    val redisKey = SHEDLOCK_PREFIX + name
    val lockValue = System.currentTimeMillis().toString()

    return withContext(Dispatchers.IO) {
        val redisClient = RedissonClientHolder.redissonClient
        val doNotHaveLock =
            redisClient.getBucket<String>(redisKey).setIfAbsent(lockValue, lockAtMostFor.toJavaDuration()).not()

        if (doNotHaveLock) throw AlreadyLockedException()

        block.invoke()
    }
}

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: Duration,
    block: suspend () -> T
): T {
    val redisKey = SHEDLOCK_PREFIX + name
    val lockValue = System.currentTimeMillis().toString()

    return withContext(Dispatchers.IO) {
        val redisClient = RedissonClientHolder.redissonClient
        val doNotHaveLock = redisClient.getBucket<String>(redisKey).setIfAbsent(lockValue, lockAtMostFor).not()

        if (doNotHaveLock) throw AlreadyLockedException()

        block.invoke()
    }
}

private const val SHEDLOCK_PREFIX = "shedlock:"