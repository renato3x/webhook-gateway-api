package dev.renato3x.application.usecase

import dev.renato3x.domain.command.CreateWebhookDeliveryCommand
import dev.renato3x.domain.exception.EndpointNotFoundException
import dev.renato3x.domain.exception.UnauthorizedEndpointAccessException
import dev.renato3x.domain.model.WebhookDelivery
import dev.renato3x.domain.port.`in`.CreateWebhookDeliveryUseCase
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.domain.port.out.WebhookDeliveryRepository

class CreateWebhookDeliveryUseCaseImpl(
    private val webhookDeliveryRepository: WebhookDeliveryRepository,
    private val endpointRepository: EndpointRepository
) : CreateWebhookDeliveryUseCase {
    override suspend fun execute(command: CreateWebhookDeliveryCommand): WebhookDelivery {
        val endpoint = endpointRepository.findById(command.endpointId)
            ?: throw EndpointNotFoundException(command.endpointId)

        if (command.userId != endpoint.userId) {
            throw UnauthorizedEndpointAccessException(command.endpointId)
        }

        val webhookDelivery = WebhookDelivery(
            endpointId = command.endpointId,
            payload = command.payload
        )

        return webhookDeliveryRepository.save(webhookDelivery)
    }
}
