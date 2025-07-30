package net.blugrid.common.domain.exception

import java.time.Instant
import java.util.UUID

/**
 * Base class for all business domain exceptions
 * Provides rich metadata for protocol-agnostic error handling
 */
abstract class DomainException(
    message: String,
    cause: Throwable? = null,
    val metadata: ErrorMetadata
) : RuntimeException(message, cause) {

    /**
     * Convenience constructor for common cases
     */
    constructor(
        message: String,
        code: String,
        cause: Throwable? = null,
        severity: ErrorSeverity = ErrorSeverity.ERROR,
        retryable: Boolean = false,
        context: Map<String, Any> = emptyMap()
    ) : this(
        message = message,
        cause = cause,
        metadata = ErrorMetadata(
            code = code,
            severity = severity,
            retryable = retryable,
            context = context
        )
    )
}

/**
 * Metadata container for rich error information
 */
data class ErrorMetadata(
    val code: String,
    val severity: ErrorSeverity = ErrorSeverity.ERROR,
    val retryable: Boolean = false,
    val correlationId: String? = null,
    val context: Map<String, Any> = emptyMap(),
    val timestamp: Instant = Instant.now(),
    val traceId: String = UUID.randomUUID().toString()
)

enum class ErrorSeverity {
    INFO, WARNING, ERROR, CRITICAL
}
