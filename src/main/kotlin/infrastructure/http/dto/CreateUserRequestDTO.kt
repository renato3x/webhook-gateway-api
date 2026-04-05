package dev.renato3x.infrastructure.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequestDTO(val username: String = "") : Validatable {
    override fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (username.isBlank()) {
            errors.add("username must not be blank")
            return errors
        }

        if (username.contains(Regex("\\s"))) {
            errors.add("username must not contain spaces")
        }

        if (username.length !in 5..16) {
            errors.add("username must be between 5 and 16 characters")
        }

        if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            errors.add("username must only contain letters, numbers and underscores")
        }

        return errors
    }
}
