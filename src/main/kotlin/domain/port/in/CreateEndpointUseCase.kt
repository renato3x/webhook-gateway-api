package dev.renato3x.domain.port.`in`

import dev.renato3x.domain.command.CreateEndpointCommand
import dev.renato3x.domain.model.Endpoint

interface CreateEndpointUseCase {
    suspend fun execute(command: CreateEndpointCommand): Endpoint
}
