package dev.renato3x.infrastructure.http

import dev.renato3x.domain.command.DispatchWebhookCommand
import dev.renato3x.domain.dto.DispatchWebhookResult
import dev.renato3x.domain.port.out.WebhookDispatcher
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json

class KtorWebhookDispatcher : WebhookDispatcher {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    override suspend fun dispatch(command: DispatchWebhookCommand): DispatchWebhookResult {
        try {
            val result = client.post(command.url.value) {
                contentType(ContentType.Application.Json)
                setBody(command.payload)
            }

            if (result.status.isSuccess()) {
                return DispatchWebhookResult.Delivered
            }

            return DispatchWebhookResult.Failed
        } catch (_: Throwable) {
            return DispatchWebhookResult.Failed
        }
    }
}