package dev.renato3x.infrastructure.http.routes

import dev.renato3x.domain.command.CreateUserCommand
import dev.renato3x.domain.command.GetUserDataCommand
import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.domain.port.`in`.GetUserDataUseCase
import dev.renato3x.infrastructure.http.dto.endpoint.EndpointItemDTO
import dev.renato3x.infrastructure.http.dto.user.CreateUserRequestDTO
import dev.renato3x.infrastructure.http.dto.user.CreateUserResponseDTO
import dev.renato3x.infrastructure.http.dto.user.GetAuthenticatedUserDataResponseDTO
import dev.renato3x.infrastructure.http.principal.UserPrincipal
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Route.userRoutes() {
    val createUserUseCase by inject<CreateUserUseCase>()
    val getUserDataUseCase by inject<GetUserDataUseCase>()

    route("users") {
        post {
            val dto = call.receive<CreateUserRequestDTO>()

            val command = CreateUserCommand(dto.username, dto.email)
            val user = createUserUseCase.execute(command)

            val response = CreateUserResponseDTO(user.apiKey)
            call.respond(HttpStatusCode.Created, response)
        }

        authenticate {
            get("me") {
                val principal = call.principal<UserPrincipal>()!!
                val result = getUserDataUseCase.execute(
                    GetUserDataCommand(
                        userId = principal.user.id!!
                    )
                )

                val user = GetAuthenticatedUserDataResponseDTO(
                    id = result.id,
                    username = result.username,
                    email = result.email,
                    endpoints = result.endpoints.map {
                        EndpointItemDTO(
                            id = it.id!!,
                            url = it.url.value,
                            nickname = it.nickname,
                            userId = it.userId,
                        )
                    }
                )

                call.respond(HttpStatusCode.OK, user)
            }
        }
    }
}
