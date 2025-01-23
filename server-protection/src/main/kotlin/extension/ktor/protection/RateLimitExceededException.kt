package extension.ktor.protection

data class RateLimitExceededException(
    override val message: String = "Rate limit exceeded",
): RuntimeException()