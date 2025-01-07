package extension.ktor.redis

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

suspend inline fun <reified T : Any> cacheable(
    key: String,
    ttl: Duration = 5.seconds,
    noinline cacheHit: suspend () -> Unit = {},
    noinline cacheMiss: suspend () -> Unit = {},
    crossinline block: suspend () -> T,
): T {
    val objectMapper = RedisObjectMapper.objectMapper
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