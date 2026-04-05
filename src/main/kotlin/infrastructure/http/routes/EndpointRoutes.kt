package dev.renato3x.infrastructure.http.routes

import dev.renato3x.domain.command.CreateEndpointCommand
import dev.renato3x.domain.port.`in`.CreateEndpointUseCase
import dev.renato3x.domain.value.Url
import dev.renato3x.infrastructure.http.dto.endpoint.CreateEndpointRequestDTO
import dev.renato3x.infrastructure.http.dto.endpoint.CreateEndpointResponseDTO
import dev.renato3x.infrastructure.http.principal.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.endpointRoutes() {
    val createEndpointUseCase by inject<CreateEndpointUseCase>()

    authenticate {
        route("endpoints") {
            post {
                val principal = call.principal<UserPrincipal>()!!
                val dto = call.receive<CreateEndpointRequestDTO>()

                val command = CreateEndpointCommand(
                    userId = principal.user.id!!,
                    url = Url(dto.url),
                    nickname = dto.nickname
                )
                val endpoint = createEndpointUseCase.execute(command)

                val response = CreateEndpointResponseDTO(
                    id = endpoint.id!!,
                    url = endpoint.url.value,
                    nickname = endpoint.nickname,
                    userId = endpoint.userId
                )

                call.respond(HttpStatusCode.Created, response)
            }
        }
    }
}