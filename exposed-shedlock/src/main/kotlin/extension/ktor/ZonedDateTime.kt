package extension.ktor

import java.time.ZoneOffset
import java.time.ZonedDateTime

internal infix fun ZonedDateTime.between(
    closedRange: ClosedRange<ZonedDateTime>,
) =  this >= closedRange.start && this <= closedRange.endInclusive

internal val now
    get() = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()