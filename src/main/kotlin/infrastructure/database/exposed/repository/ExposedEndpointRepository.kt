package dev.renato3x.infrastructure.database.exposed.repository

import dev.renato3x.domain.model.Endpoint
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.infrastructure.database.exposed.table.EndpointTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedEndpointRepository : EndpointRepository {
    override suspend fun save(endpoint: Endpoint): Endpoint {
        val id = transaction {
            EndpointTable.insertAndGetId {
                it[url] = endpoint.url
                it[nickname] = endpoint.nickname
                it[userId] = endpoint.userId
            }
        }

        return endpoint.copy(id = id.value)
    }

    override suspend fun findByUserIdAndUrl(
        userId: Int,
        url: String
    ): Endpoint? {
        val endpoint = transaction {
            EndpointTable.selectAll()
                .where {
                    (EndpointTable.userId eq userId) and (EndpointTable.url eq url)
                }
                .singleOrNull()
        }

        return endpoint?.toEndpoint()
    }

    private fun ResultRow.toEndpoint() = Endpoint(
        id = this[EndpointTable.id].value,
        url = this[EndpointTable.url],
        nickname = this[EndpointTable.nickname],
        userId = this[EndpointTable.userId].value
    )
}