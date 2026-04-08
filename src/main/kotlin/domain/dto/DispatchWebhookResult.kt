package dev.renato3x.domain.dto

sealed class DispatchWebhookResult {
    data object Delivered : DispatchWebhookResult()
    data object Failed : DispatchWebhookResult()
}
