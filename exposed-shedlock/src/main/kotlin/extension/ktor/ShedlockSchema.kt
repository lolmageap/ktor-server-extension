package extension.ktor

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.vendors.ForUpdateOption
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

internal object Shedlocks : Table("shedlock") {
    val name: Column<String> = varchar("name", 255)
    val lockedAt = datetime("locked_at").default(now)
    val lockUntil = datetime("lock_until")

    override val primaryKey = PrimaryKey(name)

    fun Query.pessimisticLock(
        mode: ForUpdateOption.PostgreSQL.MODE? = null,
    ) =
        this.forUpdate(
            ForUpdateOption.PostgreSQL.ForUpdate(
                mode,
                this@Shedlocks,
            )
        )
}

internal data class Shedlock(
    val name: String,
    val lockedAt: ZonedDateTime,
    val lockUntil: ZonedDateTime,
) {
    companion object {
        fun of(
            resultRow: ResultRow,
        ) =
            Shedlock(
                name = resultRow[Shedlocks.name],
                lockedAt = resultRow[Shedlocks.lockedAt].toZonedDateTime(),
                lockUntil = resultRow[Shedlocks.lockUntil].toZonedDateTime(),
            )
    }
}

private fun LocalDateTime.toZonedDateTime() = ZonedDateTime.of(this, UTC)