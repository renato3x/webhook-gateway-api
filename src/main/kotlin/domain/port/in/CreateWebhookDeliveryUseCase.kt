package dev.renato3x.domain.port.`in`

import dev.renato3x.domain.command.CreateWebhookDeliveryCommand
import dev.renato3x.domain.model.WebhookDelivery

interface CreateWebhookDeliveryUseCase {
    suspend fun execute(command: CreateWebhookDeliveryCommand): WebhookDelivery
}
