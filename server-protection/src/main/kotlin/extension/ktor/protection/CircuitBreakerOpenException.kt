package extension.ktor.protection

class CircuitBreakerOpenException(
    private val key: String? = null,
) : RuntimeException(
    if (key == null) "Circuit breaker is open"
    else "Circuit breaker is open for key: $key"
)