package dev.renato3x.domain.outcome

import dev.renato3x.domain.model.Endpoint

data class GetUserDataResult(
    val id: Int,
    val username: String,
    val email: String?,
    val endpoints: List<Endpoint>
)
