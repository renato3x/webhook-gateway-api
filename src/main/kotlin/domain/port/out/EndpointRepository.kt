package dev.renato3x.domain.port.out

import dev.renato3x.domain.model.Endpoint

interface EndpointRepository {
    suspend fun save(endpoint: Endpoint): Endpoint
    suspend fun findByUserIdAndUrl(userId: Int, url: String): Endpoint?
}
