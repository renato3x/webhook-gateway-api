package dev.renato3x.domain.command

data class CreateEndpointCommand(
    val url: String,
    val nickname: String,
    val userId: Int,
)
