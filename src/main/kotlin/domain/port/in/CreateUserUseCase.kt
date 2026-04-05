package dev.renato3x.domain.port.`in`

import dev.renato3x.domain.command.CreateUserCommand
import dev.renato3x.domain.model.User

interface CreateUserUseCase {
    suspend fun execute(command: CreateUserCommand): User
}
