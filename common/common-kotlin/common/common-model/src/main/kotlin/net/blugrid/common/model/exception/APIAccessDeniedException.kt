package net.blugrid.common.model.exception

import io.micronaut.http.HttpStatus

class APIAccessDeniedException(
    message: String = "Access Denied",
    details: List<Any>? = null,
    includeStackTrace: Boolean = false
) : APIException(
    apiError = DefaultAPIError(
        statusCode = HttpStatus.FORBIDDEN.code.toString(),
        code = "ACCESS_DENIED",
        message = message,
        details = details
    ),
    includeStackTrace = includeStackTrace
)
