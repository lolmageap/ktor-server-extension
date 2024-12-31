package extension.ktor.redis

class AlreadyLockedException : RuntimeException() {
    override val message: String
        get() = "Already locked"
}