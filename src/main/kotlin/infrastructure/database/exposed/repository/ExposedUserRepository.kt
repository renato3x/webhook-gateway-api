package dev.renato3x.infrastructure.database.exposed.repository

import dev.renato3x.domain.model.User
import dev.renato3x.domain.port.out.UserRepository
import dev.renato3x.infrastructure.database.exposed.table.UserTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ExposedUserRepository : UserRepository {
    override suspend fun save(user: User): User {
        val id = suspendTransaction {
            UserTable.insertAndGetId {
                it[username] = user.username
                it[apiKey] = user.apiKey
            }
        }

        return user.copy(id = id.value)
    }

    override suspend fun findByUsername(username: String): User? {
        val result = suspendTransaction {
            UserTable.selectAll().where {
                UserTable.username eq username
            }.singleOrNull()
        }

        return result?.toUser()
    }

    override suspend fun findById(id: Int): User? {
        val result = suspendTransaction {
            UserTable.selectAll().where {
                UserTable.id eq id
            }.singleOrNull()
        }

        return result?.toUser()
    }

    override suspend fun findByApiKey(apiKey: Uuid): User? {
        val result = suspendTransaction {
            UserTable.selectAll().where {
                UserTable.apiKey eq apiKey
            }.singleOrNull()
        }

        return result?.toUser()
    }

    private fun ResultRow.toUser() = User(
        id = this[UserTable.id].value,
        username = this[UserTable.username],
        apiKey = this[UserTable.apiKey]
    )
}