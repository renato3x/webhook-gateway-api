package dev.renato3x

import dev.renato3x.plugins.configureDatabases
import dev.renato3x.plugins.configureSerialization
import dev.renato3x.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty).start(wait = true)
}

fun Application.module() {
    configureDatabases()
    configureSerialization()
    configureRouting()
}
