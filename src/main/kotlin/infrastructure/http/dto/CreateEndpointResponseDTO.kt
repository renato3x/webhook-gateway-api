package dev.renato3x.infrastructure.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateEndpointResponseDTO(
    val id: Int,
    val url: String,
    val nickname: String,
    val userId: Int,
)
