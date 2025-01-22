package extension.ktor.protection

class CircuitBreakerOpenException(
    override val message: String,
) : RuntimeException()