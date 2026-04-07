package dev.renato3x.domain.port.out

import dev.renato3x.domain.model.WebhookDelivery

interface WebhookDeliveryRepository {
    suspend fun save(webhookDelivery: WebhookDelivery): WebhookDelivery
    suspend fun findPending(): List<WebhookDelivery>
    suspend fun update(webhookDelivery: WebhookDelivery): WebhookDelivery
}
