package net.blugrid.common.domain.exception

class NotFoundException(
    resourceType: String = "Resource",
    identifier: Any,
    cause: Throwable? = null
) : DomainException(
    message = "$resourceType not found: $identifier",
    cause = cause,
    metadata = ErrorMetadata(
        code = "RESOURCE_NOT_FOUND",
        severity = ErrorSeverity.ERROR,
        retryable = false,
        context = mapOf(
            "resourceType" to resourceType,
            "identifier" to identifier.toString()
        )
    )
)
