package dev.renato3x.domain.command

data class CreateWebhookDeliveryCommand(
    val endpointId: Int,
    val payload: String,
    val userId: Int
)
