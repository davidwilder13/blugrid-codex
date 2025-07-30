package net.blugrid.common.domain.exception

/**
 * Exception thrown when tenant context is missing or invalid
 */
class TenantContextException(
    message: String = "No tenant context available for operation",
    cause: Throwable? = null,
    correlationId: String? = null
) : DomainException(
    message = message,
    cause = cause,
    metadata = ErrorMetadata(
        code = "TENANT_CONTEXT_MISSING",
        severity = ErrorSeverity.ERROR,
        retryable = false,
        correlationId = correlationId,
        context = mapOf("operation" to "tenant_context_resolution")
    )
)
