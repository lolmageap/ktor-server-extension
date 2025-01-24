package serverprotection

import extension.ktor.protection.rateLimiter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class RateLimiterTest: StringSpec({
    "rate limiter test" {
        val key = "rate-limiter"
        val number = AtomicInteger(0)

        (1..50).map {
            async {
                runCatching {
                    rateLimiter(key, 10, 1.seconds) {
                        number.incrementAndGet()
                    }
                }
            }
        }.awaitAll()

        number.get() shouldBe 10
    }
})