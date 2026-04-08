package dev.renato3x.domain.outcome

sealed class DispatchWebhookResult {
    data object Delivered : DispatchWebhookResult()
    data object Failed : DispatchWebhookResult()
}
