package dev.renato3x.infrastructure.http.dto

import dev.renato3x.domain.exception.DomainException
import dev.renato3x.domain.value.Url
import kotlinx.serialization.Serializable

@Serializable
data class CreateEndpointRequestDTO(
    val url: String = "",
    val nickname: String = "",
    val userId: Int = 0,
) : Validatable {
    override fun validate(): List<String> {
        val errors = mutableListOf<String>()

        // nickname validation
        if (nickname.isBlank()) {
            errors.add("nickname must not be blank")
            return errors
        }

        if (nickname.length !in 6..30) {
            errors.add("nickname must be between 6 and 30")
        }

        // url validation
        try {
            Url(url)
        } catch (e: DomainException) {
            errors.add(e.message!!)
        }

        // user id validation
        if (userId <= 0) {
            errors.add("user_id must be greater than 0")
        }

        return errors
    }
}
