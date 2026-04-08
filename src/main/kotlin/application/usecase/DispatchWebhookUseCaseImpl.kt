package dev.renato3x.application.usecase

import dev.renato3x.domain.command.DispatchWebhookCommand
import dev.renato3x.domain.outcome.DispatchWebhookResult
import dev.renato3x.domain.model.WebhookDeliveryStatus
import dev.renato3x.domain.port.`in`.DispatchWebhookUseCase
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.domain.port.out.WebhookDeliveryRepository
import dev.renato3x.domain.port.out.WebhookDispatcher
import kotlin.math.pow
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

class DispatchWebhookUseCaseImpl(
    private val webhookDeliveryRepository: WebhookDeliveryRepository,
    private val endpointRepository: EndpointRepository,
    private val webhookDispatcher: WebhookDispatcher
) : DispatchWebhookUseCase {
    override suspend fun execute() {
        val maxRetries = 5
        val deliveries = webhookDeliveryRepository.findPending()

        if (deliveries.isEmpty()) {
            println("No pending webhook deliveries found")
            return
        }

        for (delivery in deliveries) {
            val endpoint = endpointRepository.findById(delivery.endpointId)!!

            println(
                "Dispatching webhook ${endpoint.url} using" +
                        " payload ${delivery.payload}." +
                        " Attempt ${delivery.attempts + 1} of $maxRetries"
            )

            val result = webhookDispatcher.dispatch(
                DispatchWebhookCommand(
                    url = endpoint.url,
                    payload = delivery.payload,
                )
            )

            if (result is DispatchWebhookResult.Failed) {
                val attempts = delivery.attempts + 1

                val (status, nextRetryAt) = if (attempts == maxRetries) {
                    WebhookDeliveryStatus.FAILED to null
                } else {
                    val minutesToAdd = 2.0.pow(delivery.attempts.toDouble()).toInt().minutes
                    val nextRetryAt = Clock.System.now().plus(minutesToAdd)
                    WebhookDeliveryStatus.PENDING to nextRetryAt
                }

                val updatedDelivery = delivery.copy(
                    attempts = attempts,
                    nextRetryAt = nextRetryAt,
                    status = status
                )

                if (attempts == maxRetries) {
                    println(
                        "Webhook delivery to ${endpoint.url} failed after $attempts attempts. Marking as FAILED."
                    )
                } else {
                    println(
                        "Webhook delivery to ${endpoint.url} failed on attempt $attempts." +
                                " Scheduling next retry at $nextRetryAt." +
                                " ${maxRetries - attempts} left"
                    )
                }

                webhookDeliveryRepository.update(updatedDelivery)
                continue
            }

            println(
                "Webhook delivery to ${endpoint.url} succeeded on attempt ${delivery.attempts + 1}." +
                        " Marking as SUCCESS."
            )
            webhookDeliveryRepository.update(
                delivery
                    .copy(status = WebhookDeliveryStatus.SUCCESS)
            )
        }
    }
}