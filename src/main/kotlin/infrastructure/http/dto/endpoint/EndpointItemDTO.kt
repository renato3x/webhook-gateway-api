package dev.renato3x.infrastructure.http.dto.endpoint

import kotlinx.serialization.Serializable

@Serializable
data class EndpointItemDTO(
    val id: Int,
    val url: String,
    val nickname: String,
    val userId: Int,
)