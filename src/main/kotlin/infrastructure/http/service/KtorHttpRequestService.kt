package dev.renato3x.infrastructure.http.service

import dev.renato3x.domain.dto.HttpResponse
import dev.renato3x.domain.port.out.HttpRequestService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.*

private val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

class KtorHttpRequestService : HttpRequestService {
    override suspend fun post(
        url: String,
        body: Any,
        headers: Map<String, String>
    ): HttpResponse {
        val result = client.post(url) {
            setBody(body)
            headers.forEach { (key, value) -> header(key, value) }
        }

        return HttpResponse(statusCode = result.status.value)
    }
}
