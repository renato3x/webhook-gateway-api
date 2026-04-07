package dev.renato3x.domain.port.out

import dev.renato3x.domain.dto.HttpResponse

interface HttpRequestService {
    suspend fun post(
        url: String,
        body: Any,
        headers: Map<String, String> = emptyMap()
    ): HttpResponse
}
