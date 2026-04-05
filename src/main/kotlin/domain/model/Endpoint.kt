package dev.renato3x.domain.model

import dev.renato3x.domain.value.Url

data class Endpoint(
    val id: Int? = null,
    val url: Url,
    val nickname: String,
    val userId: Int,
)
