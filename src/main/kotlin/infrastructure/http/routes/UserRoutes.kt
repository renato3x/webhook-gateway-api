package dev.renato3x.infrastructure.http.routes

import dev.renato3x.domain.command.CreateUserCommand
import dev.renato3x.domain.exception.UsernameAlreadyExistsException
import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.infrastructure.http.dto.CreateUserRequestDTO
import dev.renato3x.infrastructure.http.dto.CreateUserResponseDTO
import dev.renato3x.infrastructure.http.exception.InvalidRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Route.userRoutes(createUserUseCase: CreateUserUseCase) {
    route("users") {
        post {
            try {
                val dto = call.receive<CreateUserRequestDTO>()
                dto.validate()
                val command = CreateUserCommand(dto.username)
                val user = createUserUseCase.execute(command)
                val response = CreateUserResponseDTO(user.apiKey)
                call.respond(
                    HttpStatusCode.Created,
                    response
                )
            } catch (e: UsernameAlreadyExistsException) {
                call.respond(
                    HttpStatusCode.Conflict,
                    mapOf("error" to e.message)
                )
            } catch (e: InvalidRequestException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to e.message)
                )
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "An unexpected error occurred")
                )
            }
        }
    }
}