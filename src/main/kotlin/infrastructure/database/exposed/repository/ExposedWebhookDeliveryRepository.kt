package dev.renato3x.infrastructure.database.exposed.repository

import dev.renato3x.domain.model.WebhookDelivery
import dev.renato3x.domain.port.out.WebhookDeliveryRepository
import dev.renato3x.infrastructure.database.exposed.table.WebhookDeliveryTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

class ExposedWebhookDeliveryRepository : WebhookDeliveryRepository {
    override suspend fun save(webhookDelivery: WebhookDelivery): WebhookDelivery {
        val id = suspendTransaction {
            WebhookDeliveryTable.insertAndGetId {
                it[endpoint] = webhookDelivery.endpointId
                it[payload] = webhookDelivery.payload
            }
        }

        return webhookDelivery.copy(id = id.value)
    }

    private fun ResultRow.toWebhookDelivery() = WebhookDelivery(
        id = this[WebhookDeliveryTable.id].value,
        endpointId = this[WebhookDeliveryTable.endpoint].value,
        payload = this[WebhookDeliveryTable.payload],
        attempts = this[WebhookDeliveryTable.attempts],
        nextRetryAt = this[WebhookDeliveryTable.nextRetryAt],
        status = this[WebhookDeliveryTable.status]
    )
}