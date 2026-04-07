package dev.renato3x.infrastructure.di

import dev.renato3x.application.usecase.CreateEndpointUseCaseImpl
import dev.renato3x.application.usecase.CreateUserUseCaseImpl
import dev.renato3x.application.usecase.CreateWebhookDeliveryUseCaseImpl
import dev.renato3x.application.usecase.DispatchWebhookUseCaseImpl
import dev.renato3x.domain.port.`in`.CreateEndpointUseCase
import dev.renato3x.domain.port.`in`.CreateUserUseCase
import dev.renato3x.domain.port.`in`.CreateWebhookDeliveryUseCase
import dev.renato3x.domain.port.`in`.DispatchWebhookUseCase
import dev.renato3x.domain.port.out.EndpointRepository
import dev.renato3x.domain.port.out.HttpRequestService
import dev.renato3x.domain.port.out.UserRepository
import dev.renato3x.domain.port.out.WebhookDeliveryRepository
import dev.renato3x.infrastructure.database.exposed.repository.ExposedEndpointRepository
import dev.renato3x.infrastructure.database.exposed.repository.ExposedUserRepository
import dev.renato3x.infrastructure.database.exposed.repository.ExposedWebhookDeliveryRepository
import dev.renato3x.infrastructure.http.service.KtorHttpRequestService
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { ExposedUserRepository() }
    single<CreateUserUseCase> { CreateUserUseCaseImpl(get()) }

    single<EndpointRepository> { ExposedEndpointRepository() }
    single<CreateEndpointUseCase> { CreateEndpointUseCaseImpl(get(), get()) }

    single<WebhookDeliveryRepository> { ExposedWebhookDeliveryRepository() }
    single<CreateWebhookDeliveryUseCase> {
        CreateWebhookDeliveryUseCaseImpl(get(), get())
    }

    single<HttpRequestService> { KtorHttpRequestService() }
    single<DispatchWebhookUseCase> {
        DispatchWebhookUseCaseImpl(get(), get(), get())
    }
}
