package net.blugrid.integration.grpc.model.exception

import jakarta.validation.ConstraintViolation

data class ConstraintViolationDetail(val field: String, val description: String)

class InvalidRequestException(val violations: List<ConstraintViolationDetail>) : RuntimeException()

fun Set<ConstraintViolation<*>>.throwIfInvalid() {
    if (this.isNotEmpty()) {
        throw InvalidRequestException(map {
            ConstraintViolationDetail(it.propertyPath.toString(), it.message)
        })
    }
}
