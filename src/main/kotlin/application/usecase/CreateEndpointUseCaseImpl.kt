package dev.renato3x.application.usecase

import dev.renato3x.domain.command.CreateEndpointCommand
import dev.renato3x.domain.exception.EndpointAlreadyExistsException
import dev.renato3x.domain.exception.UserNotFoundException
import dev.renato3x.domain.model.Endpoint
import dev.renato3x.domain.port.`in`.CreateEndpointUseCase
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.domain.port.out.UserRepository

class CreateEndpointUseCaseImpl(
    private val endpointRepository: EndpointRepository,
    private val userRepository: UserRepository
) : CreateEndpointUseCase {
    override suspend fun execute(command: CreateEndpointCommand): Endpoint {
        val endpoint = endpointRepository.findByUserIdAndUrl(
            userId = command.userId,
            url = command.url
        )

        if (endpoint != null) {
            throw EndpointAlreadyExistsException()
        }

        userRepository.findById(command.userId) ?: throw UserNotFoundException()

        val newEndpoint = Endpoint(
            url = command.url,
            nickname = command.nickname,
            userId = command.userId,
        )

        return endpointRepository.save(newEndpoint)
    }
}