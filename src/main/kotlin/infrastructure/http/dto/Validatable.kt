package dev.renato3x.infrastructure.http.dto

interface Validatable {
    fun validate(): List<String>
}
