package dev.renato3x.infrastructure.http.dto.user

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class CreateUserResponseDTO(val apiKey: Uuid)
