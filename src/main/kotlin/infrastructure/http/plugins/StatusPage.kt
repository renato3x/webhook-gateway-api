package dev.renato3x.infrastructure.http.plugins

import dev.renato3x.domain.exception.UsernameAlreadyExistsException
import dev.renato3x.infrastructure.http.dto.ErrorResponseDTO
import dev.renato3x.infrastructure.http.exception.RequestException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<UsernameAlreadyExistsException> { call, cause ->
            val response = ErrorResponseDTO(
                error = cause.message!!,
                statusCode = HttpStatusCode.Conflict.value,
            )

            call.respond(
                HttpStatusCode.Conflict,
                response,
            )
        }

        exception<RequestValidationException> { call, cause ->
            val reasons = cause.reasons.map { JsonPrimitive(it) }
            val response = ErrorResponseDTO(
                error = "Some fields are missing or invalid",
                statusCode = HttpStatusCode.BadRequest.value,
                details = mapOf(
                    "errors" to JsonArray(reasons)
                )
            )

            call.respond(
                HttpStatusCode.BadRequest,
                response,
            )
        }

        exception<RequestException> { call, cause ->
            val response = ErrorResponseDTO(
                error = cause.message!!,
                statusCode = cause.statusCode.value,
                details = cause.details
            )

            call.respond(
                cause.statusCode,
                response,
            )
        }

        exception<Throwable> { call, _ ->
            val response = ErrorResponseDTO(
                error = "An unexpected error occurred",
                statusCode = HttpStatusCode.InternalServerError.value,
            )

            call.respond(
                HttpStatusCode.InternalServerError,
                response
            )
        }
    }
}