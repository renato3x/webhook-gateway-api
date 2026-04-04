package dev.renato3x.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.renato3x.model.Endpoints
import dev.renato3x.model.Users
import io.ktor.server.config.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        Database.connect(hikari(config))
        transaction {
            SchemaUtils.create(Users, Endpoints)
        }
    }

    private fun hikari(config: ApplicationConfig): HikariDataSource {
        val driverClassName = config.property("hikari.driverClassName").getString()
        val jdbcUrl = config.property("hikari.jdbcUrl").getString()
        val username = config.property("hikari.username").getString()
        val password = config.property("hikari.password").getString()
        val maximumPoolSize = config.property("hikari.maximumPoolSize").getString().toInt()
        val config = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            this.maximumPoolSize = maximumPoolSize
        }
        return HikariDataSource(config)
    }
}