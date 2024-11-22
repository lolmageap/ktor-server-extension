package extension.ktor.exposed

import org.jetbrains.exposed.sql.ResultRow

internal fun ResultRow.toShedLock() = Shedlock.of(this)