package extension.ktor.redis

import extension.ktor.redis.RedisObjectMapper.objectMapper
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

suspend inline fun <reified T : Any> cacheable(
    key: String,
    ttl: kotlin.time.Duration = 5.seconds,
    noinline cacheHit: suspend () -> Unit = {},
    noinline cacheMiss: suspend () -> Unit = {},
    crossinline block: suspend () -> T,
): T {
    val objectMapper = objectMapper
    val redisClient = RedissonClientHolder.redissonClient
    val data = redisClient.getBucket<String>(key).get()

    if (data != null) {
        cacheHit.invoke()
        return objectMapper.readValue(data, T::class.java) as T
    } else {
        cacheMiss.invoke()
    }

    return block().apply {
        val serializedValue = objectMapper.writeValueAsString(this)
        redisClient.getBucket<String>(key).set(serializedValue, ttl.toJavaDuration())
    }
}

suspend inline fun <reified T : Any> cacheable(
    key: String,
    ttl: java.time.Duration = java.time.Duration.ofSeconds(5),
    noinline cacheHit: suspend () -> Unit = {},
    noinline cacheMiss: suspend () -> Unit = {},
    crossinline block: suspend () -> T,
): T {
    val objectMapper = objectMapper
    val redisClient = RedissonClientHolder.redissonClient
    val data = redisClient.getBucket<String>(key).get()

    if (data != null) {
        cacheHit.invoke()
        return objectMapper.readValue(data, T::class.java) as T
    } else {
        cacheMiss.invoke()
    }

    return block().apply {
        val serializedValue = objectMapper.writeValueAsString(this)
        redisClient.getBucket<String>(key).set(serializedValue, ttl)
    }
}