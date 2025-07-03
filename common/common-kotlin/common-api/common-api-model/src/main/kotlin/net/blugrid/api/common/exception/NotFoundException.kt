package net.blugrid.api.common.exception

import io.micronaut.http.HttpStatus

class NotFoundException(
    message: String = "Resource Not Found",
    details: List<Any>? = null,
    includeStackTrace: Boolean = false
) : APIException(
    apiError = DefaultAPIError(
        statusCode = HttpStatus.NOT_FOUND.code.toString(),
        code = "NOT_FOUND",
        message = message,
        details = details
    ),
    includeStackTrace = includeStackTrace
)
