package extension.ktor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.*
import io.ktor.server.application.*

val mapper = jacksonObjectMapper()

val ApplicationCall.pathVariable
    get() = PathVariable(this)

inline fun <reified T : Any> ApplicationCall.queryParams(): T =
    this.request.queryParameters.toClass()

inline fun <reified T : Any> Parameters.toClass(): T {
    val map = this.entries().associate {
        it.key to (it.value.getOrNull(0)
            ?: throw IllegalArgumentException("Missing value for key ${it.key}"))
    }
    return mapper.convertValue(map, T::class.java)
}