package dev.renato3x.domain.port.out

import dev.renato3x.domain.model.Endpoint
import dev.renato3x.domain.value.Url

interface EndpointRepository {
    suspend fun save(endpoint: Endpoint): Endpoint
    suspend fun findByUserIdAndUrl(userId: Int, url: Url): Endpoint?
    suspend fun findById(id: Int): Endpoint?
    suspend fun findAllByUserId(userId: Int): List<Endpoint>
}
