package extension.ktor.protection

suspend fun <T> rateLimiter(
    key: String,
    limit: Long,
    period: kotlin.time.Duration,
    block: suspend () -> T,
) {
    TODO("Not implemented yet")
    block()
}