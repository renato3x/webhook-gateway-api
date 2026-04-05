package dev.renato3x.domain.value

import dev.renato3x.domain.exception.DomainException
import java.net.URI

@JvmInline
value class Url(val value: String) {
    init {
        if (value.isBlank()) {
            throw DomainException("url must not be blank")
        }

        try {
            val uri = URI(value)

            if (uri.scheme.isNullOrBlank() || uri.host.isNullOrBlank()) {
                throw DomainException("url must be a valid URL")
            }

            if (uri.scheme !in listOf("http", "https")) {
                throw DomainException("url must be http or https")
            }

            if (uri.host in listOf("localhost", "127.0.0.1")) {
                throw DomainException("url must not point to a local address")
            }
        } catch (e: DomainException) {
            throw e
        } catch (_: Exception) {
            throw DomainException("url must be a valid URL")
        }
    }
}