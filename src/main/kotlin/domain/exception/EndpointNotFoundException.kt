package dev.renato3x.domain.exception

class EndpointNotFoundException(endpointId: Int) : DomainException("Endpoint with id $endpointId not found")
