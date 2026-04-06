package dev.renato3x.infrastructure.http.plugins

import dev.renato3x.infrastructure.database.DataSourceFactory
import dev.renato3x.infrastructure.database.FlywaySetup
import dev.renato3x.infrastructure.database.exposed.ExposedDatabaseSetup
import io.ktor.server.application.*

fun Application.configureDatabases() {
    val dataSource = DataSourceFactory.create(environment.config)
    FlywaySetup.migrate(dataSource)
    ExposedDatabaseSetup.init(dataSource)
}
