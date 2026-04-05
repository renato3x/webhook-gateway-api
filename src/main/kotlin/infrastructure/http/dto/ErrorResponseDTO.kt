package dev.renato3x.infrastructure.http.dto

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class ErrorResponseDTO(
    val error: String,
    val statusCode: Int,
    val timestamp: Instant = Clock.System.now()
)
