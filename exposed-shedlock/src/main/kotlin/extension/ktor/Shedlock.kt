package extension.ktor

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Duration
import java.time.LocalDateTime

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: Duration,
    block: suspend () -> T,
) {
    reactiveTransaction {
        val resultRow = Shedlocks.selectAll()
            .where { Shedlocks.name eq name }
            .forUpdate()
            .singleOrNull()

        val now = LocalDateTime.now()

        if (resultRow == null) {
            insertNewShedlock(name, now, lockAtMostFor)
        } else {
            val shedLock = resultRow.toShedLock()

            if (now between shedLock.lockedAt..shedLock.lockUntil) {
                throw AlreadyLockedException()
            }

            Shedlocks.update({ Shedlocks.name eq name }) {
                it[lockedAt] = now
                it[lockUntil] = now + lockAtMostFor
            }
        }
    }

    block.invoke()
}

private fun insertNewShedlock(
    name: String,
    now: LocalDateTime,
    lockAtMostFor: Duration,
): ResultRow {
    Shedlocks.insert {
        it[Shedlocks.name] = name
        it[lockedAt] = now
        it[lockUntil] = now + lockAtMostFor
    }

    return Shedlocks.selectAll()
        .where { Shedlocks.name eq name }
        .forUpdate()
        .single()
}