package extension.ktor.redis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.time.toJavaDuration

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: kotlin.time.Duration,
    resetLockUntilAfterComplete: Boolean = true,
    block: suspend () -> T,
) {
    shedlock(name, lockAtMostFor.toJavaDuration(), resetLockUntilAfterComplete, block)
}

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: java.time.Duration,
    resetLockUntilAfterComplete: Boolean = true,
    block: suspend () -> T,
) {
    val redisLock = RedissonClientHolder.redissonClient.getLock(SHEDLOCK_PREFIX + name)

    try {
        val isLocked =
            withContext(Dispatchers.IO) {
                redisLock.tryLockAsync(lockAtMostFor.toMillis(), MILLISECONDS).get()
            }

        if (isLocked) throw AlreadyLockedException()
        block.invoke()
    } catch (e: Exception) {
        redisLock.unlockAsync()
        throw e
    } finally {
        if (resetLockUntilAfterComplete) redisLock.unlockAsync()
    }
}

private const val SHEDLOCK_PREFIX = "shedlock:"