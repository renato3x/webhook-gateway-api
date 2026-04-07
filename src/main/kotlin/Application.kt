package dev.renato3x

import dev.renato3x.infrastructure.http.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureDatabases()
    configureSerialization()
    configureValidation()
    configureStatusPage()
    configureAuthentication()
    configureWorkers()
    configureRouting()
}
