package dev.renato3x.infrastructure.database.exposed

import com.zaxxer.hikari.HikariDataSource
import dev.renato3x.infrastructure.database.exposed.table.EndpointTable
import dev.renato3x.infrastructure.database.exposed.table.UserTable
import dev.renato3x.infrastructure.database.exposed.table.WebhookDeliveryTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object ExposedDatabaseSetup {
    fun init(dataSource: HikariDataSource) {
        Database.connect(dataSource)
        transaction {
            SchemaUtils.create(
                UserTable,
                EndpointTable,
                WebhookDeliveryTable
            )
        }
    }
}