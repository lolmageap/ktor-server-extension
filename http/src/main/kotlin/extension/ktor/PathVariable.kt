package extension.ktor

import io.ktor.server.application.*

class PathVariable(
    private val call: ApplicationCall,
) {
    operator fun get(
        key: String,
    ) =
        call.parameters[key] ?: throw IllegalArgumentException("$key is required.")
}