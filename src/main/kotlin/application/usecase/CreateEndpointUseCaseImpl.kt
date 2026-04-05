package dev.renato3x.application.usecase

import dev.renato3x.domain.command.CreateEndpointCommand
import dev.renato3x.domain.exception.EndpointAlreadyExistsException
import dev.renato3x.domain.model.Endpoint
import dev.renato3x.domain.port.`in`.CreateEndpointUseCase
import dev.renato3x.domain.port.out.EndpointRepository

class CreateEndpointUseCaseImpl(
    private val repository: EndpointRepository
) : CreateEndpointUseCase {
    override suspend fun execute(command: CreateEndpointCommand): Endpoint {
        val endpoint = repository.findByUserIdAndUrl(
            userId = command.userId,
            url = command.url
        )

        if (endpoint != null) {
            throw EndpointAlreadyExistsException()
        }

        val newEndpoint = Endpoint(
            url = command.url,
            nickname = command.nickname,
            userId = command.userId,
        )

        return repository.save(newEndpoint)
    }
}