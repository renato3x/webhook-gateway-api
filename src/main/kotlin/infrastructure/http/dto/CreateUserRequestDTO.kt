package dev.renato3x.infrastructure.http.dto

import dev.renato3x.infrastructure.http.exception.InvalidRequestException
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequestDTO(val username: String) {
    fun validate() {
        if (username.isBlank()) {
            throw InvalidRequestException("Username cannot be blank")
        }

        if (username.length !in 5..16) {
            throw InvalidRequestException("Username must be between 5 and 16 characters")
        }
    }
}
