package dev.renato3x.infrastructure.http.dto.user

import dev.renato3x.infrastructure.http.dto.endpoint.EndpointItemDTO
import kotlinx.serialization.Serializable

@Serializable
data class GetAuthenticatedUserDataResponseDTO(
    val id: Int,
    val username: String,
    val email: String?,
    val endpoints: List<EndpointItemDTO>,
)
