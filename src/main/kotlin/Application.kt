package dev.renato3x

import dev.renato3x.infrastructure.http.plugins.configureDatabases
import dev.renato3x.infrastructure.http.plugins.configureSerialization
import dev.renato3x.infrastructure.http.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDatabases()
    configureSerialization()
    configureRouting()
}
