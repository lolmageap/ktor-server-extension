package extension.ktor.redis

class AlreadyLockedException(
    override val message: String = "Already locked",
) : RuntimeException(message)