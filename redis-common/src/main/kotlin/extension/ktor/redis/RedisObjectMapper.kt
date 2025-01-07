package extension.ktor.redis

import com.fasterxml.jackson.databind.ObjectMapper

object RedisObjectMapper {
    val objectMapper = ObjectMapper()
}

/**
 * RedisObjectMapper.objectMapper.apply {
 *     enable(SerializationFeature.INDENT_OUTPUT)
 *     disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
 * }
 */