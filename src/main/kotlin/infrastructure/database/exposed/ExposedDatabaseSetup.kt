package dev.renato3x.infrastructure.database.exposed

import dev.renato3x.infrastructure.database.exposed.table.EndpointTable
import dev.renato3x.infrastructure.database.exposed.table.UserTable
import dev.renato3x.infrastructure.database.exposed.table.WebhookDeliveryTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.sql.DataSource

object ExposedDatabaseSetup {
    fun init(dataSource: DataSource) {
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
