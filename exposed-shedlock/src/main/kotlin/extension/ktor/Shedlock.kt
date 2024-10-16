package extension.ktor

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: Duration,
    block: suspend () -> T,
) {
    reactiveTransaction {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        try {
            val resultRow = Shedlocks.selectAll()
                .where { Shedlocks.name eq name }
                .pessimisticLock()
                .singleOrNull()
                ?: insertNewShedlock(name, lockAtMostFor)

            val shedLock = resultRow.toShedLock()

            if (now between shedLock.lockedAt..shedLock.lockUntil) {
                throw RuntimeException("Already locked")
            }

            Shedlocks.update({ Shedlocks.name eq name }) {
                val updatedAt = ZonedDateTime.now(ZoneOffset.UTC)
                it[lockedAt] = updatedAt.toLocalDateTime()
                it[lockUntil] = updatedAt.toLocalDateTime() + lockAtMostFor
            }
        } catch (e: IllegalStateException) {
            throw RuntimeException("Already locked")
        }
    }

    try {
        block.invoke()
    } finally {
        reactiveTransaction {
            Shedlocks.update({ Shedlocks.name eq name }) {
                it[lockUntil] = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()
            }
        }
    }
}

private fun insertNewShedlock(
    name: String,
    duration: Duration,
): ResultRow {
    Shedlocks.insert {
        it[Shedlocks.name] = name
        it[lockedAt] = now
        it[lockUntil] = now + duration
    }

    return Shedlocks.selectAll()
        .where { Shedlocks.name eq name }
        .pessimisticLock()
        .single()
}