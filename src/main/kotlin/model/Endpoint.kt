package dev.renato3x.model

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object Endpoints : IntIdTable("endpoints") {
    val url = text("url")
    val nickname = varchar("nickname", 30)
    val user = reference(
        "user_id",
        Users,
        ReferenceOption.CASCADE
    )
}

class Endpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Endpoint>(Endpoints)

    var url by Endpoints.url
    var nickname by Endpoints.nickname
    var user by User referencedOn Endpoints.user
}
