package dev.renato3x

import dev.renato3x.plugins.configureDatabases
import dev.renato3x.plugins.configureSerialization
import dev.renato3x.plugins.configureRouting
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
