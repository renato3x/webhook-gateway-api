package dev.renato3x.infrastructure.http.plugins

import dev.renato3x.domain.port.`in`.DispatchWebhookUseCase
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.minutes

fun Application.configureWorkers() {
    val dispatchWebhookUseCase by inject<DispatchWebhookUseCase>()
    monitor.subscribe(ApplicationStarted) {
        launch(Dispatchers.IO) {
            while (true) {
                dispatchWebhookUseCase.execute()
                delay(1.minutes)
            }
        }
    }
}