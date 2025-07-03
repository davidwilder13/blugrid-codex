package net.blugrid.api.common.exception

import io.micronaut.http.HttpStatus


class APIInternalServerException(
    message: String = "API Internal Server Error",
    details: List<Any>? = null,
    includeStackTrace: Boolean = false
) : APIException(
    apiError = DefaultAPIError(
        statusCode = HttpStatus.INTERNAL_SERVER_ERROR.code.toString(),
        code = "INTERNAL_SERVER_ERROR",
        message = message,
        details = details
    ),
    includeStackTrace = includeStackTrace
)
