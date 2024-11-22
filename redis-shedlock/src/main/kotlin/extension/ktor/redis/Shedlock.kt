package extension.ktor.redis

import io.lettuce.core.RedisClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.redisson.Redisson
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

// TODO: 테스트 해야함
suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: java.time.Duration,
    resetLockUntilAfterComplete: Boolean = true,
    block: suspend () -> T,
) {
    val redisLock = Redisson.create().getLock(SHEDLOCK_PREFIX + name)

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