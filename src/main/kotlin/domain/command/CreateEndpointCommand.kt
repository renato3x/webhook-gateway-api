package dev.renato3x.domain.command

import dev.renato3x.domain.value.Url

data class CreateEndpointCommand(
    val url: Url,
    val nickname: String,
    val userId: Int,
)
