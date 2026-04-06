package dev.renato3x.domain.exception

class UnauthorizedEndpointAccessException(
    endpointId: Int
) : DomainException("Invalid user for endpoint with id $endpointId")
