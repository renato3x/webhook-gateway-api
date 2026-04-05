package dev.renato3x.infrastructure.di

import dev.renato3x.application.usecase.CreateUserUseCaseImpl
import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.domain.port.out.UserRepository
import dev.renato3x.infrastructure.database.exposed.repository.ExposedUserRepository
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { ExposedUserRepository() }
    single<CreateUserUseCase> { CreateUserUseCaseImpl(get()) }
}
