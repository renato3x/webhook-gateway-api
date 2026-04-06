package dev.renato3x.infrastructure.http.routes

import dev.renato3x.domain.command.CreateWebhookDeliveryCommand
import dev.renato3x.domain.port.`in`.CreateWebhookDeliveryUseCase
import dev.renato3x.infrastructure.http.dto.gateway.CreateWebhookDeliveryRequestDTO
import dev.renato3x.infrastructure.http.principal.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.gatewayRoutes() {
    val createWebhookDeliveryUseCase by inject<CreateWebhookDeliveryUseCase>()

    authenticate {
        post("/dispatch") {
            val principal = call.principal<UserPrincipal>()!!
            val request = call.receive<CreateWebhookDeliveryRequestDTO>()

            val command = CreateWebhookDeliveryCommand(
                endpointId = request.endpointId,
                payload = request.payload.toString(),
                userId = principal.user.id!!
            )

            createWebhookDeliveryUseCase.execute(command)
            call.respond(HttpStatusCode.Accepted)
        }
    }
}
