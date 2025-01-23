package extension.ktor.protection

import extension.ktor.protection.CircuitBreakerState.*
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

val circuitBreakers = ConcurrentHashMap<String, CircuitBreaker>()

sealed class CircuitBreakerState {
    data object Closed : CircuitBreakerState()
    data object HalfOpen : CircuitBreakerState()
    data class Open(val openedAt: Instant) : CircuitBreakerState()
}

data class CircuitBreaker(
    val failureCount: AtomicInteger = AtomicInteger(0),
    val state: AtomicReference<CircuitBreakerState> = AtomicReference(Closed),
    val failureThreshold: Int,
    val resetTimeout: kotlin.time.Duration,
) {
    constructor(
        failureCount: AtomicInteger = AtomicInteger(0),
        state: AtomicReference<CircuitBreakerState> = AtomicReference(Closed),
        failureThreshold: Int,
        resetTimeout: java.time.Duration,
    ) : this(
        failureCount = failureCount,
        state = state,
        failureThreshold = failureThreshold,
        resetTimeout = resetTimeout.toKotlinDuration(),
    )
}

suspend fun <T> circuitBreaker(
    key: String,
    failureThreshold: Int = 10,
    resetTimeout: kotlin.time.Duration = 5.seconds,
    block: suspend () -> T,
): T {
    val breaker = circuitBreakers.computeIfAbsent(key) {
        CircuitBreaker(
            failureThreshold = failureThreshold,
            resetTimeout = resetTimeout,
        )
    }

    return when (val currentState = breaker.state.get()) {
        is Closed -> breaker.executeInClosed(block)

        is Open -> {
            val closedDuration = Instant.now().toEpochMilli() - currentState.openedAt.toEpochMilli()

            if (closedDuration.milliseconds >= breaker.resetTimeout) {
                breaker.state.set(HalfOpen)
                breaker.executeInHalfOpen(block)
            } else {
                throw CircuitBreakerOpenException(key)
            }
        }

        is HalfOpen -> breaker.executeInHalfOpen(block)
    }
}

suspend fun <T> circuitBreaker(
    key: String,
    failureThreshold: Int = 10,
    resetTimeout: java.time.Duration = java.time.Duration.ofSeconds(5),
    block: suspend () -> T,
): T {
    val breaker = circuitBreakers.computeIfAbsent(key) {
        CircuitBreaker(
            failureThreshold = failureThreshold,
            resetTimeout = resetTimeout,
        )
    }

    return when (val currentState = breaker.state.get()) {
        is Closed -> breaker.executeInClosed(block)

        is Open -> {
            val closedDuration = Instant.now().toEpochMilli() - currentState.openedAt.toEpochMilli()

            if (closedDuration.milliseconds >= breaker.resetTimeout) {
                breaker.state.set(HalfOpen)
                breaker.executeInHalfOpen(block)
            } else {
                throw CircuitBreakerOpenException(key)
            }
        }

        is HalfOpen -> breaker.executeInHalfOpen(block)
    }
}

private suspend fun <T> CircuitBreaker.executeInClosed(
    block: suspend () -> T,
) =
    try {
        val result = block.invoke()
        this.reset()
        result
    } catch (e: Exception) {
        if (this.failureCount.incrementAndGet() >= this.failureThreshold) {
            this.openCircuit()
        }
        throw e
    }

private suspend fun <T> CircuitBreaker.executeInHalfOpen(
    block: suspend () -> T,
) =
    try {
        val result = block.invoke()
        this.reset()
        result
    } catch (e: Exception) {
        this.openCircuit()
        throw e
    }

private fun CircuitBreaker.openCircuit() {
    this.state.set(Open(Instant.now()))
}

private fun CircuitBreaker.reset() {
    this.state.set(Closed)
    this.failureCount.set(0)
}