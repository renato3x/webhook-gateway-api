package dev.renato3x.application.usecase

import dev.renato3x.domain.command.CreateUserCommand
import dev.renato3x.domain.exception.UsernameAlreadyExistsException
import dev.renato3x.domain.model.User
import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.domain.port.out.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateUserUseCaseImpl(
    private val userRepository: UserRepository
) : CreateUserUseCase {
    override suspend fun execute(command: CreateUserCommand): User {
        val user = userRepository.findByUsername(command.username)

        if (user != null) {
            throw UsernameAlreadyExistsException()
        }

        val newUser = User(
            username = command.username,
            apiKey = Uuid.random()
        )

        return userRepository.save(newUser)
    }
}
