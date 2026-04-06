package dev.renato3x.infrastructure.database.exposed.table

import dev.renato3x.domain.model.WebhookDeliveryStatus
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

private const val STATUS_ENUM_COLUMN_LENGTH = 20

object WebhookDeliveryTable : IntIdTable("webhook_deliveries") {
    val endpoint = reference("endpoint_id", EndpointTable)
    val payload = text("payload")
    val attempts = integer("attempts").default(0)
    val nextRetryAt = timestamp("next_retry_at").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val status = enumerationByName(
        "status",
        STATUS_ENUM_COLUMN_LENGTH,
        WebhookDeliveryStatus::class
    ).default(WebhookDeliveryStatus.PENDING)
}
