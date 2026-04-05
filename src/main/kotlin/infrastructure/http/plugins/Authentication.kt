package dev.renato3x.infrastructure.http.plugins

import dev.renato3x.domain.port.out.UserRepository
import dev.renato3x.infrastructure.http.dto.ErrorResponseDTO
import dev.renato3x.infrastructure.http.principal.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.apikey.apiKey
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Application.configureAuthentication() {
    val userRepository by inject<UserRepository>()

    install(Authentication) {
        apiKey {
            headerName = "X-API-Key"
            validate { apiKeyString ->
                val apiKey = try {
                    Uuid.parse(apiKeyString)
                } catch (_: Exception) {
                    return@validate null
                }

                val user = userRepository.findByApiKey(apiKey)
                user?.let { UserPrincipal(it)}
            }
            challenge { call ->
                val response = ErrorResponseDTO(
                    error = "Missing or invalid API key",
                    statusCode = HttpStatusCode.BadRequest.value
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    response,
                )
            }
        }
    }
}