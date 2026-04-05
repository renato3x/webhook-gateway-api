package dev.renato3x.infrastructure.http.exception

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonElement

class RequestException(
    message: String,
    val statusCode: HttpStatusCode,
    val details: Map<String, JsonElement> = emptyMap(),
) : RuntimeException(message)
