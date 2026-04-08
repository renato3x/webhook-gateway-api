package dev.renato3x.domain.port.out

import dev.renato3x.domain.command.DispatchWebhookCommand
import dev.renato3x.domain.dto.DispatchWebhookResult

interface WebhookDispatcher {
    suspend fun dispatch(command: DispatchWebhookCommand): DispatchWebhookResult
}
