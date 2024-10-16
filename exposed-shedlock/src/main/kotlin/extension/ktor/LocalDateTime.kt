package extension.ktor

import java.time.LocalDateTime

internal infix fun LocalDateTime.between(
    closedRange: ClosedRange<LocalDateTime>,
) = this >= closedRange.start && this <= closedRange.endInclusive