package cache

import extension.ktor.redis.RedissonClientHolder
import extension.ktor.redis.cacheable
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.redisson.Redisson
import org.redisson.config.Config
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class CacheableTest : StringSpec({
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

    "cacheable test" {
        val key = "key"
        database[key] = "value"
        var number = 0

        (0..10).map {
            async {
                val data =
                    cacheable(key, 5.seconds) {
                        number++
                        database.find(key)
                    }

                database.getValue(key) shouldBe data
            }
        }.awaitAll()

        number shouldBeGreaterThan 1
    }
})

private val database = mutableMapOf<String, String>()
private suspend fun MutableMap<String, String>.find(key: String): String {
    delay(1.seconds)
    return this.getValue(key)
}