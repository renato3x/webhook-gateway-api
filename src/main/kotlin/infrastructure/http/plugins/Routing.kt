package dev.renato3x.infrastructure.http.plugins

import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.infrastructure.http.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(createUserUseCase: CreateUserUseCase) {
    routing {
        route("v1") {
            get("ping") {
                call.respondText("pong")
            }
            userRoutes(createUserUseCase)
        }
    }
}
