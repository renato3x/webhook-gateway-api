package dev.renato3x.infrastructure.database.exposed

import org.jetbrains.exposed.v1.jdbc.Database
import javax.sql.DataSource

object ExposedDatabaseSetup {
    fun init(dataSource: DataSource) {
        Database.connect(dataSource)
    }
}
