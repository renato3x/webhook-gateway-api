package dev.renato3x.infrastructure.http.dto.endpoint

import dev.renato3x.domain.exception.DomainException
import dev.renato3x.domain.value.Url
import dev.renato3x.infrastructure.http.dto.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class CreateEndpointRequestDTO(
    val url: String = "",
    val nickname: String = ""
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

        return errors
    }
}
