package dev.renato3x.infrastructure.http.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("v1") {
            get("ping") {
                call.respondText("pong")
            }
        }
    }
}
