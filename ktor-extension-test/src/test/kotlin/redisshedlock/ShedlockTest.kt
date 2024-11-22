package redisshedlock

import extension.ktor.redis.shedlock
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

class RedisShedlockTest : StringSpec({
    lateinit var redisClient: RedisAsyncCommands<String, String>

    beforeTest {
        redisClient = RedisClient.create().connect(RedisURI.create("redis://localhost:6379")).async()
    }

    afterEach {
        redisClient.flushall()
    }

    "Verify that shedlock ensures the logic is executed only once when multiple applications start simultaneously with no existing data in redis" {
        val name = "test"
        val duration = 1.toMinutes()
        val count = AtomicInteger(0)

        coroutineScope {
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
        }

        count.get() shouldBe 1
    }

    "Verify that shedlock ensures the logic is executed only once when multiple applications start simultaneously with existing data in redis" {
        val name = "test"
        val duration = 1.toMinutes()
        val count = AtomicInteger(0)

        shedlock(name, 100.toMilliSeconds()) { count.getAndIncrement() }
        delay(100)

        coroutineScope {
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
        }

        count.get() shouldBe 2
    }
})

fun Int.toMinutes(): Duration = Duration.ofMinutes(this.toLong())
fun Int.toMilliSeconds(): Duration = Duration.ofMillis(this.toLong())