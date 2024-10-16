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
        try {
            val resultRow = Shedlocks.selectAll()
                .where { Shedlocks.name eq name }
                .pessimisticLock()
                .singleOrNull()

            if (resultRow == null) {
                val newShedlock = insertNewShedlock(name, lockAtMostFor)
                newShedlock.toShedLock().lockedAt
            } else {
                val shedLock = resultRow.toShedLock()
                val now = shedLock.lockedAt

                if (now between now..shedLock.lockUntil) {
                    throw RuntimeException("Already locked")
                }

                Shedlocks.update({ Shedlocks.name eq name }) {
                    it[lockedAt] = now
                    it[lockUntil] = now + lockAtMostFor
                }

                now
            }
        } catch (e: Exception) {
            throw RuntimeException("Already locked")
        }
    }

    block.invoke()
}

private fun insertNewShedlock(
    name: String,
    duration: Duration,
): ResultRow {
    val now = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()
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