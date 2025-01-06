package extension.ktor.redis

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

suspend inline fun <reified T: Any> cacheable(
    key: String,
    ttl: Duration = 5.seconds,
    cacheHit: () -> Unit = {},
    cacheMiss: () -> Unit = {},
    block: () -> T,
): T {
    val objectMapper = ObjectMapper()
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