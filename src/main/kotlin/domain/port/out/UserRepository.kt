package dev.renato3x.domain.port.out

import dev.renato3x.domain.model.User

interface UserRepository {
    suspend fun save(user: User): User
    suspend fun findByUsername(username: String): User?
    suspend fun findById(id: Int): User?
}
