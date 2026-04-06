package dev.renato3x.infrastructure.database

import org.flywaydb.core.Flyway
import javax.sql.DataSource

object FlywaySetup {
    fun migrate(dataSource: DataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .load()

        flyway.migrate()
    }
}
