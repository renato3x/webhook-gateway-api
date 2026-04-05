package dev.renato3x.domain.model

import kotlin.time.Instant

enum class WebhookDeliveryStatus {
    PENDING,
    SUCCESS,
    FAILED
}

data class WebhookDelivery(
    val id: Int? = null,
    val endpointId: Int,
    val payload: String,
    val attempts: Int = 0,
    val nextRetryAt: Instant? = null,
    val status: WebhookDeliveryStatus = WebhookDeliveryStatus.PENDING,
)
