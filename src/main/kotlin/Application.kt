package dev.renato3x

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
