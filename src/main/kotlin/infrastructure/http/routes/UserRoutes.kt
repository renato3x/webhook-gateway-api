package dev.renato3x.infrastructure.http.routes

import dev.renato3x.domain.command.CreateUserCommand
import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.infrastructure.http.dto.CreateUserRequestDTO
import dev.renato3x.infrastructure.http.dto.CreateUserResponseDTO
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Route.userRoutes() {
    val createUserUseCase by inject<CreateUserUseCase>()

    route("users") {
        post {
            val dto = call.receive<CreateUserRequestDTO>()

            val command = CreateUserCommand(dto.username)
            val user = createUserUseCase.execute(command)

            val response = CreateUserResponseDTO(user.apiKey)
            call.respond(HttpStatusCode.Created, response)
        }
    }
}