package net.blugrid.common.domain.exception

class InternalServerException(
    message: String = "An unexpected error occurred",
    cause: Throwable? = null
) : DomainException(
    message = message,
    cause = cause,
    metadata = ErrorMetadata(
        code = "INTERNAL_SERVER_ERROR",
        severity = ErrorSeverity.CRITICAL,
        retryable = false
    )
)
