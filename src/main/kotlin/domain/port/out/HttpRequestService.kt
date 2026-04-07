package domain.port.out

import domain.dto.HttpResponse

interface HttpRequestService {
    suspend fun post(
        url: String,
        body: Any,
        headers: Map<String, String> = emptyMap()
    ): HttpResponse
}
