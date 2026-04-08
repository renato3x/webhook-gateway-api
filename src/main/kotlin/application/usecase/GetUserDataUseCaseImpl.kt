package dev.renato3x.application.usecase

import dev.renato3x.domain.command.GetUserDataCommand
import dev.renato3x.domain.exception.UserNotFoundException
import dev.renato3x.domain.outcome.GetUserDataResult
import dev.renato3x.domain.port.`in`.GetUserDataUseCase
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.domain.port.out.UserRepository

class GetUserDataUseCaseImpl(
    private val userRepository: UserRepository,
    private val endpointRepository: EndpointRepository
) : GetUserDataUseCase {
    override suspend fun execute(command: GetUserDataCommand): GetUserDataResult {
        val user = userRepository.findById(command.userId) ?: throw UserNotFoundException()
        val endpoints = endpointRepository.findAllByUserId(command.userId)

        return GetUserDataResult(
            id = user.id!!,
            username = user.username,
            email = user.email,
            endpoints = endpoints
        )
    }
}
