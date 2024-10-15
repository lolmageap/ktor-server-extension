package extension.ktor

import org.jetbrains.exposed.sql.ResultRow

internal fun ResultRow.toShedLock() = Shedlock.of(this)