package dev.renato3x.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import javax.sql.DataSource

object DataSourceFactory {
    fun create(config: ApplicationConfig): DataSource {
        val driverClassName = config.property("hikari.driverClassName").getString()
        val jdbcUrl = config.property("hikari.jdbcUrl").getString()
        val username = config.property("hikari.username").getString()
        val password = config.property("hikari.password").getString()
        val maximumPoolSize = config.property("hikari.maximumPoolSize").getString().toInt()
        val dataSourceConfig = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            this.maximumPoolSize = maximumPoolSize
        }
        return HikariDataSource(dataSourceConfig)
    }
}
