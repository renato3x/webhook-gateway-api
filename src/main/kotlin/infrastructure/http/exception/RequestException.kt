package dev.renato3x.infrastructure.http.exception

import io.ktor.http.HttpStatusCode

class RequestException(val statusCode: HttpStatusCode, message: String) : RuntimeException(message)
