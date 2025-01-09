package cache

import extension.ktor.redis.RedissonClientHolder
import io.kotest.core.spec.style.StringSpec
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import org.redisson.Redisson
import org.redisson.config.Config

class CacheLockingTest : StringSpec({
    lateinit var redisClient: RedisAsyncCommands<String, String>
    val database = mutableMapOf<String, String>()

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

    "cache locking test" {

    }
})