package extension.ktor

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration

class ShedlockTestStringSpec : StringSpec({
    beforeTest {
        embeddedServer(Netty, port = 8080) {
            shedlockModule()
        }.start()
    }

    afterEach {
        transaction {
            SchemaUtils.drop(Shedlocks)
        }
    }

    "여러개의 application이 동시에 실행 될 때 shedlock이 적용 되어 로직이 한번만 실행되는지 확인한다." {
        val name = "test"
        val duration = 1.toMinutes()
        var count = 0

        coroutineScope {
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
            launch { runCatching { shedlock(name, duration) { count += 1 } } }
        }

        count shouldBe 1
    }
})

fun Int.toMinutes(): Duration = Duration.ofMinutes(this.toLong())