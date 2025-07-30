package net.blugrid.common.domain.exception

class AccessDeniedException(
    resource: String = "resource",
    action: String = "access",
    cause: Throwable? = null
) : DomainException(
    message = "Access denied for action '$action' on $resource",
    cause = cause,
    metadata = ErrorMetadata(
        code = "ACCESS_DENIED",
        severity = ErrorSeverity.ERROR,
        retryable = false,
        context = mapOf(
            "resource" to resource,
            "action" to action
        )
    )
)
