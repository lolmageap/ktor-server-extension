package extension.ktor.exposed

class AlreadyLockedException : RuntimeException() {
    override val message: String
        get() = "Already locked"
}