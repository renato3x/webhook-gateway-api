package dev.renato3x.domain.port.`in`

import dev.renato3x.domain.command.GetUserDataCommand
import dev.renato3x.domain.outcome.GetUserDataResult

interface GetUserDataUseCase {
    suspend fun execute(command: GetUserDataCommand): GetUserDataResult
}
