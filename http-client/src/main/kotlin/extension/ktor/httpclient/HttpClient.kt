package extension.ktor.httpclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*

@OptIn(InternalAPI::class)
suspend inline fun <reified RETURN_TYPE : Any> HttpClient.call(
    url: String,
    httpMethod: HttpMethod,
    headers: Map<String, String> = emptyMap(),
    body: Any? = null,
) =
    this.use {
        this.request(url) {
            method = httpMethod

            headers {
                headers.forEach { (key, value) ->
                    this.append(key, value)
                }
            }

            if (httpMethod == HttpMethod.Post || httpMethod == HttpMethod.Put) {
                if (body != null) {
                    this.body = body
                    this.bodyType = typeInfo<LinkedHashMap<String, Any>>()
                    this.headers {
                        append(HttpHeaders.ContentType, ContentType.Application.Json)
                    }
                }
            }
        }
    }.body<RETURN_TYPE>()