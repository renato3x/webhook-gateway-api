package dev.renato3x.domain.command

import dev.renato3x.domain.value.Url

data class DispatchWebhookCommand(
    val url: Url,
    val payload: String
)
