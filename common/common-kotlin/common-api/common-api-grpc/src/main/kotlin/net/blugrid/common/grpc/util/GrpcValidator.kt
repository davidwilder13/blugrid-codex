package net.blugrid.common.grpc.util

import io.micronaut.validation.validator.Validator
import jakarta.validation.ConstraintViolationException

object GrpcValidator {
    inline fun <reified T> validate(validator: Validator, block: () -> T): T {
        val model = block()
        val violations = validator.validate(model)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
        return model
    }
}
