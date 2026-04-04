package dev.renato3x.model

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object Users : IntIdTable("users") {
    val username = varchar("username", 16).uniqueIndex()
    val apiKey = uuid("api_key").uniqueIndex()
}

@OptIn(ExperimentalUuidApi::class)
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var apiKey by Users.apiKey
    val endpoints by Endpoint referrersOn Endpoints.user
}
