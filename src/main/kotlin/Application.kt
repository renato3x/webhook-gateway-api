package dev.renato3x

import dev.renato3x.application.usecase.CreateUserUseCaseImpl
import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.domain.port.out.UserRepository
import dev.renato3x.infrastructure.database.exposed.repository.ExposedUserRepository
import dev.renato3x.infrastructure.http.plugins.configureDatabases
import dev.renato3x.infrastructure.http.plugins.configureSerialization
import dev.renato3x.infrastructure.http.plugins.configureRouting
import dev.renato3x.infrastructure.http.plugins.configureStatusPage
import dev.renato3x.infrastructure.http.plugins.configureValidation
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDatabases()
    configureSerialization()
    configureValidation()
    configureStatusPage()

    val userRepository: UserRepository = ExposedUserRepository()
    val createUserUseCase: CreateUserUseCase = CreateUserUseCaseImpl(userRepository)
    configureRouting(createUserUseCase)
}
