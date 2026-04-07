package dev.renato3x.application.usecase

import dev.renato3x.domain.model.WebhookDeliveryStatus
import dev.renato3x.domain.port.`in`.DispatchWebhookUseCase
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.domain.port.out.HttpRequestService
import dev.renato3x.domain.port.out.WebhookDeliveryRepository
import kotlin.math.pow
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

class DispatchWebhookUseCaseImpl(
    private val webhookDeliveryRepository: WebhookDeliveryRepository,
    private val endpointRepository: EndpointRepository,
    private val httpRequestService: HttpRequestService
) : DispatchWebhookUseCase {
    // TODO: Add real logging
    override suspend fun execute() {
        val maxRetries = 5
        val deliveries = webhookDeliveryRepository.findPending()

        if (deliveries.isEmpty()) {
            println("No pending webhooks for delivery found")
            return
        }

        println("Found ${deliveries.size} pending webhooks for delivery. Initiating dispatch...")
        for (delivery in deliveries) {
            val endpoint = endpointRepository.findById(delivery.endpointId)!!

            if (delivery.attempts == maxRetries) {
                println(
                    "Delivery for ${endpoint.url.value} already" +
                            " reached the maximum attempts. Marking as FAILED"
                )
                webhookDeliveryRepository.update(
                    delivery
                        .copy(status = WebhookDeliveryStatus.FAILED)
                )
                continue
            }

            println("Initiating delivery for ${endpoint.url.value}. Attempt #${delivery.attempts + 1}")

            val result = httpRequestService.post(
                endpoint.url.value,
                delivery.payload,
                mapOf(
                    "Content-Type" to "application/json"
                )
            )

            if (result.statusCode !in 200..299) {
                val minutesToAdd = 2.0.pow(delivery.attempts.toDouble()).toInt().minutes
                val nextRetryAt = Clock.System.now().plus(minutesToAdd)
                println(
                    "Request for ${endpoint.url.value} with payload failed." +
                            " ${maxRetries - (delivery.attempts + 1)} retries left." +
                            " Next retry at $nextRetryAt"
                )
                webhookDeliveryRepository.update(
                    delivery
                        .copy(
                            attempts = delivery.attempts + 1,
                            nextRetryAt = nextRetryAt
                        )
                )
                continue
            }

            println("Request for ${endpoint.url.value} with payload succeeded. Marking as SUCCESS")
            webhookDeliveryRepository.update(
                delivery
                    .copy(status = WebhookDeliveryStatus.SUCCESS)
            )
        }
    }
}