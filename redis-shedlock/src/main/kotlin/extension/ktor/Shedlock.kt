package extension.ktor

import kotlin.time.toJavaDuration

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: kotlin.time.Duration,
    block: suspend () -> T,
) {
    shedlock(name, lockAtMostFor.toJavaDuration(), block)
}

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: java.time.Duration,
    block: suspend () -> T,
) {
    block.invoke()
}