package dev.renato3x.infrastructure.database.exposed.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object EndpointTable : IntIdTable("endpoints") {
    val url = text("url")
    val nickname = varchar("nickname", 30)
    val userId = reference(
        "user_id",
        UserTable,
        ReferenceOption.CASCADE
    )
}
