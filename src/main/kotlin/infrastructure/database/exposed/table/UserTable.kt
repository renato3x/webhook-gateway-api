package dev.renato3x.infrastructure.database.exposed.table

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object UserTable : IntIdTable("users") {
    val username = varchar("username", 16).uniqueIndex()
    val apiKey = uuid("api_key").uniqueIndex()
    val email = varchar("email", 255).uniqueIndex().nullable()
}
