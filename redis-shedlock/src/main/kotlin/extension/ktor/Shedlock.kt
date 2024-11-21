package extension.ktor

import io.lettuce.core.RedisClient
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
    val redisAsyncCommands = RedisClient.create().connect().async()
    val redisLock = Redisson.create().getLock(SHEDLOCK_PREFIX + name)

    if (redisLock.isLocked) throw AlreadyLockedException()

    redisLock.lock(lockAtMostFor.toMillis(), MILLISECONDS)

    try {
        block()
    } finally {
        if (resetLockUntilAfterComplete) redisLock.unlock()
    }
}

private const val SHEDLOCK_PREFIX = "shedlock:"