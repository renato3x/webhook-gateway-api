package dev.renato3x.infrastructure.http.plugins

import dev.renato3x.infrastructure.http.dto.CreateUserRequestDTO
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<CreateUserRequestDTO> { dto ->
            val errors = mutableListOf<String>()

            if (dto.username.isBlank()) {
                errors.add("username must not be blank")
            }

            if (dto.username.length !in 5..16) {
                errors.add("username must be between 5 and 16 characters")
            }

            if (errors.isEmpty()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(errors)
            }
        }
    }
}