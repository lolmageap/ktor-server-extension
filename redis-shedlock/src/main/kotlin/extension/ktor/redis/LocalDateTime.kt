package extension.ktor.redis

import java.time.LocalDateTime

internal infix fun LocalDateTime.between(
    closedRange: ClosedRange<LocalDateTime>,
) = this >= closedRange.start && this <= closedRange.endInclusive