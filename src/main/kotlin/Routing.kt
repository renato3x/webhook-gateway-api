package dev.renato3x

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("v1") {
            get {
                call.respond(
                    mapOf(
                        "message" to "Hello World"
                    )
                )
            }
        }
    }
}
