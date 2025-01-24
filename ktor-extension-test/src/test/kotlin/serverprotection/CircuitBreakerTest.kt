package serverprotection

import extension.ktor.protection.CircuitBreakerOpenException
import extension.ktor.protection.circuitBreaker
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class CircuitBreakerTest : StringSpec({

    "circuitBreaker should be closed when success" {
        var count = 0

        0.until(10).map {
            async {
                circuitBreaker(SUCCESS_KEY, 10, 5.seconds) {
                    count++
                }
            }
        }.awaitAll()

        count shouldBe 10
    }

    "circuitBreaker should be open when fail" {
        var count = 0

        0.until(10).map {
            async {
                runCatching {
                    circuitBreaker(FAIL_KEY, 10, 5.seconds) {
                        throw RuntimeException()
                    }
                }
            }
        }.awaitAll()

        shouldThrow<CircuitBreakerOpenException> {
            circuitBreaker(FAIL_KEY, 10, 5.seconds) {
                count++
            }
        }

        count shouldBe 0
    }

    "circuitBreaker should be half open after reset timeout" {
        var count = 0

        0.until(10).map {
            async {
                runCatching {
                    circuitBreaker(RESET_KEY, 10, 1.seconds) {
                        throw RuntimeException()
                    }
                }
            }
        }.awaitAll()

        shouldThrow<CircuitBreakerOpenException> {
            circuitBreaker(RESET_KEY, 10, 1.seconds) {
                count++
            }
        }

        count shouldBe 0

        delay(1.seconds)

        0.until(10).map {
            async {
                circuitBreaker(RESET_KEY, 10, 5.seconds) {
                    count++
                }
            }
        }.awaitAll()

        count shouldBe 10
    }

})

private const val SUCCESS_KEY = "success"
private const val FAIL_KEY = "fail"
private const val RESET_KEY = "reset"