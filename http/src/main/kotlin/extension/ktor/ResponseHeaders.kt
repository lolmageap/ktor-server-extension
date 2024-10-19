package extension.ktor

import io.ktor.server.response.*

var ResponseHeaders.accessToken: String?
    get() = get("Authorization")
        ?: throw NoSuchElementException("access token is not found")
    set(value) = append("Authorization", value ?: "")