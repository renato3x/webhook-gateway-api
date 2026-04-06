package dev.renato3x.infrastructure.http.dto.gateway

import dev.renato3x.infrastructure.http.dto.Validatable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CreateWebhookDeliveryRequestDTO(
    val endpointId: Int = 0,
    val payload: JsonObject = JsonObject(emptyMap()),
) : Validatable {
    override fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (endpointId <= 0) {
            errors.add("endpoint_id must be greater than 0")
        }

        if (payload.isEmpty()) {
            errors.add("payload must not be empty")
        }

        return errors
    }
}
