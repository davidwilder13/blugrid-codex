package net.blugrid.common.domain.exception

/**
 * Resource conflict exception - domain exception
 */
class ResourceConflictException(
    resourceType: String,
    identifier: Any,
    conflictReason: String = "Resource conflict",
    cause: Throwable? = null
) : DomainException(
    message = "$resourceType conflict: $conflictReason",
    cause = cause,
    metadata = ErrorMetadata(
        code = "RESOURCE_CONFLICT",
        severity = ErrorSeverity.ERROR,
        retryable = false,
        context = mapOf(
            "resourceType" to resourceType,
            "identifier" to identifier.toString(),
            "conflictReason" to conflictReason
        )
    )
)
