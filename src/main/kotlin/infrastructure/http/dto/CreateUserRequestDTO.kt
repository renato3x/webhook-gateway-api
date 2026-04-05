package dev.renato3x.infrastructure.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequestDTO (val username: String)
