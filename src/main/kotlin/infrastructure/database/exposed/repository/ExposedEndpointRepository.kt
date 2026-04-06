package dev.renato3x.infrastructure.database.exposed.repository

import dev.renato3x.domain.model.Endpoint
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.domain.value.Url
import dev.renato3x.infrastructure.database.exposed.table.EndpointTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

class ExposedEndpointRepository : EndpointRepository {
    override suspend fun save(endpoint: Endpoint): Endpoint {
        val result = suspendTransaction {
            EndpointTable.insertReturning {
                it[url] = endpoint.url.value
                it[nickname] = endpoint.nickname
                it[userId] = endpoint.userId
            }.single()
        }

        return result.toEndpoint()
    }

    override suspend fun findByUserIdAndUrl(
        userId: Int,
        url: Url
    ): Endpoint? {
        val endpoint = suspendTransaction {
            EndpointTable.selectAll()
                .where {
                    (EndpointTable.userId eq userId) and (EndpointTable.url eq url.value)
                }
                .singleOrNull()
        }

        return endpoint?.toEndpoint()
    }

    override suspend fun findById(id: Int): Endpoint? {
        val endpoint = suspendTransaction {
            EndpointTable.selectAll()
                .where { EndpointTable.id eq id }
                .singleOrNull()
        }

        return endpoint?.toEndpoint()
    }

    private fun ResultRow.toEndpoint() = Endpoint(
        id = this[EndpointTable.id].value,
        url = Url(this[EndpointTable.url]),
        nickname = this[EndpointTable.nickname],
        userId = this[EndpointTable.userId].value
    )
}