package dev.renato3x.infrastructure.di

import dev.renato3x.application.usecase.CreateEndpointUseCaseImpl
import dev.renato3x.application.usecase.CreateUserUseCaseImpl
import dev.renato3x.domain.port.`in`.CreateEndpointUseCase
import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.domain.port.out.UserRepository
import dev.renato3x.infrastructure.database.exposed.repository.ExposedEndpointRepository
import dev.renato3x.infrastructure.database.exposed.repository.ExposedUserRepository
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { ExposedUserRepository() }
    single<CreateUserUseCase> { CreateUserUseCaseImpl(get()) }

    single<EndpointRepository> { ExposedEndpointRepository() }
    single<CreateEndpointUseCase> { CreateEndpointUseCaseImpl(get(), get()) }
}
