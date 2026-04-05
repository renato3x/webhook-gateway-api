package dev.renato3x.domain.model

data class Endpoint(
    val id: Int? = null,
    val url: String,
    val nickname: String,
    val userId: Int,
)
