package distributelock

import extension.ktor.redis.RedissonClientHolder
import extension.ktor.redis.distributedLock
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.redisson.Redisson
import org.redisson.config.Config
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class DistributedLockTest : StringSpec({
    lateinit var redisClient: RedisAsyncCommands<String, String>

    beforeTest {
        redisClient =
            RedisClient.create()
                .connect(
                    RedisURI.create("redis://localhost:6379")
                )
                .async()

        RedissonClientHolder.redissonClient =
            Redisson.create(
                Config().apply {
                    useSingleServer().address = "redis://localhost:6379"
                }
            )
    }

    afterEach {
        redisClient.flushall()
        RedissonClientHolder.redissonClient.keys.flushall()
    }

    "distributed lock test" {
        val name = "test1"
        val duration = 5.seconds
        val count = AtomicInteger(0)

        coroutineScope {
            repeat(50) { launch { runCatching { distributedLock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { distributedLock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { distributedLock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { distributedLock(name, duration) { count.getAndIncrement() } } } }
        }

        count.get() shouldBe 200
    }
})