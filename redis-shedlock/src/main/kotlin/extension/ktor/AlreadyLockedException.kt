package extension.ktor

class AlreadyLockedException : RuntimeException() {
    override val message: String
        get() = "Already locked"
}