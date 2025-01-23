package extension.ktor.redis

data class RateLimitExceededException(
    override val message: String = "Rate limit exceeded",
) : RuntimeException(message)