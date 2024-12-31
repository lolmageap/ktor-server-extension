package extension.ktor.redis

import org.redisson.api.RedissonClient

object RedissonClientHolder {
    lateinit var redissonClient: RedissonClient
}