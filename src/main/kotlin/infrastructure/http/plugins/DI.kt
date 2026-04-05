package dev.renato3x.infrastructure.http.plugins

import dev.renato3x.infrastructure.di.appModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    install(Koin) {
        modules(appModule)
    }
}