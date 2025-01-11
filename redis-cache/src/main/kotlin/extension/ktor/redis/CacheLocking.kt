package extension.ktor.redis

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

suspend inline fun <reified T : Any> cacheLocking(
    key: String,
    ttl: kotlin.time.Duration,
    leaseTime: kotlin.time.Duration = 3.seconds,
    crossinline block: suspend () -> T,
): T {
    val objectMapper = RedisObjectMapper.objectMapper
    val redisClient = RedissonClientHolder.redissonClient
    val data = redisClient.getBucket<String>(key).get() ?: null

    return if (data != null) {
        objectMapper.readValue(data, T::class.java) as T
    } else withContext(IO) {
        distributedLock(MANGLING_PREFIX + key, leaseTime, ttl) {
            val doubleCheckedValue = redisClient.getBucket<String>(key).get()
            if (doubleCheckedValue != null) {
                return@distributedLock objectMapper.readValue(doubleCheckedValue, T::class.java) as T
            }

            block.invoke().also { value ->
                val serializedValue = objectMapper.writeValueAsString(value)
                redisClient.getBucket<String>(key).setIfAbsent(serializedValue, ttl.toJavaDuration())
            }
        }
    }
}

suspend inline fun <reified T : Any> cacheLocking(
    key: String,
    ttl: java.time.Duration,
    leaseTime: java.time.Duration = java.time.Duration.ofSeconds(3),
    crossinline block: suspend () -> T,
): T {
    val objectMapper = RedisObjectMapper.objectMapper
    val redisClient = RedissonClientHolder.redissonClient
    val data = redisClient.getBucket<String>(key).get() ?: null

    return if (data != null) {
        objectMapper.readValue(data, T::class.java) as T
    } else withContext(IO) {
        distributedLock(MANGLING_PREFIX + key, leaseTime, ttl) {
            val doubleCheckedValue = redisClient.getBucket<String>(key).get()
            if (doubleCheckedValue != null) {
                return@distributedLock objectMapper.readValue(doubleCheckedValue, T::class.java) as T
            }

            block.invoke().also { value ->
                val serializedValue = objectMapper.writeValueAsString(value)
                redisClient.getBucket<String>(key).setIfAbsent(serializedValue, ttl)
            }
        }
    }
}

val MANGLING_PREFIX = "distributed-lock:${UUID.randomUUID()}"