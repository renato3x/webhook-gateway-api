package dev.renato3x.infrastructure.database.exposed.repository

import dev.renato3x.domain.model.WebhookDelivery
import dev.renato3x.domain.model.WebhookDeliveryStatus
import dev.renato3x.domain.port.out.WebhookDeliveryRepository
import dev.renato3x.infrastructure.database.exposed.table.WebhookDeliveryTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update

class ExposedWebhookDeliveryRepository : WebhookDeliveryRepository {
    override suspend fun save(webhookDelivery: WebhookDelivery): WebhookDelivery {
        val result = suspendTransaction {
            WebhookDeliveryTable.insertReturning {
                it[endpointId] = webhookDelivery.endpointId
                it[payload] = webhookDelivery.payload
            }.single()
        }

        return result.toWebhookDelivery()
    }

    override suspend fun findPending(): List<WebhookDelivery> {
        val result = suspendTransaction {
            WebhookDeliveryTable.selectAll()
                .where { WebhookDeliveryTable.status eq WebhookDeliveryStatus.PENDING }
                .orderBy(WebhookDeliveryTable.createdAt, SortOrder.ASC)
                .toList()
        }

        return result.map { it.toWebhookDelivery() }
    }

    override suspend fun update(webhookDelivery: WebhookDelivery): WebhookDelivery {
        val result = suspendTransaction {
            WebhookDeliveryTable.update({ WebhookDeliveryTable.id eq webhookDelivery.endpointId }) {
                it[endpointId] = webhookDelivery.endpointId
                it[payload] = webhookDelivery.payload
                it[attempts] = webhookDelivery.attempts
                it[nextRetryAt] = webhookDelivery.nextRetryAt
                it[status] = webhookDelivery.status
            }

            WebhookDeliveryTable.selectAll()
                .where { WebhookDeliveryTable.id eq webhookDelivery.id!! }
                .single()
        }

        return result.toWebhookDelivery()
    }

    private fun ResultRow.toWebhookDelivery() = WebhookDelivery(
        id = this[WebhookDeliveryTable.id].value,
        endpointId = this[WebhookDeliveryTable.endpointId].value,
        payload = this[WebhookDeliveryTable.payload],
        attempts = this[WebhookDeliveryTable.attempts],
        nextRetryAt = this[WebhookDeliveryTable.nextRetryAt],
        createdAt = this[WebhookDeliveryTable.createdAt],
        status = this[WebhookDeliveryTable.status]
    )
}