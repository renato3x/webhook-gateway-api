package dev.renato3x.plugins

import dev.renato3x.database.DatabaseFactory
import io.ktor.server.application.*

fun Application.configureDatabases() {
    DatabaseFactory.init(environment.config)
}