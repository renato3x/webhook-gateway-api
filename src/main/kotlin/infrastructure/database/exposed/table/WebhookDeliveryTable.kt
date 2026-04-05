package dev.renato3x.infrastructure.database.exposed.table

import dev.renato3x.domain.model.WebhookDeliveryStatus
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object WebhookDeliveryTable : IntIdTable("webhook_deliveries") {
    val endpoint = reference("endpoint_id", EndpointTable)
    val payload = text("payload")
    val attempts = integer("attempts").default(0)
    val nextRetryAt = datetime("next_retry_at").nullable()
    val status = enumeration("status", WebhookDeliveryStatus::class)
        .default(WebhookDeliveryStatus.PENDING)
}
