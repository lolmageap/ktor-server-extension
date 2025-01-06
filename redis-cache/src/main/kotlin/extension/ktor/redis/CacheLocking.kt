package extension.ktor.redis

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

suspend inline fun <reified T : Any> cacheableLocking(
    key: String,
    ttl: Duration = 5.seconds,
    crossinline block: suspend () -> T,
): T {
    val objectMapper = ObjectMapper()
    val redisClient = RedissonClientHolder.redissonClient
    val data = redisClient.getBucket<String>(key).get() ?: null

    return if (data != null) {
        objectMapper.readValue(data, T::class.java) as T
    } else withContext(IO) {
        distributedLock(key, 1.seconds, ttl) {
            val doubleCheckedValue = redisClient.getBucket<String>(key).get()
            if (doubleCheckedValue != null) {
                return@distributedLock objectMapper.readValue(doubleCheckedValue, T::class.java) as T
            }

            block.invoke().also { value ->
                val serializedValue = objectMapper.writeValueAsString(value)
                redisClient.getBucket<String>(key).set(serializedValue, ttl.toJavaDuration())
            }
        }
    }
}
