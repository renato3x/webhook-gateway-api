package dev.renato3x.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class User(
    val id: Int? = null,
    val username: String,
    val apiKey: Uuid,
    val email: String? = null,
)
