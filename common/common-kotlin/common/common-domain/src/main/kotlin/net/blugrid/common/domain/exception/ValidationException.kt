package net.blugrid.common.domain.exception

/**
 * Validation violation detail for structured error reporting
 */
data class ValidationViolation(
    val field: String?,
    val message: String,
    val rejectedValue: Any? = null,
    val code: String? = null
)

/**
 * Base class for validation-related domain exceptions
 */
abstract class ValidationException(
    message: String,
    val violations: List<ValidationViolation>,
    cause: Throwable? = null,
    code: String = "VALIDATION_ERROR"
) : DomainException(
    message = message,
    cause = cause,
    metadata = ErrorMetadata(
        code = code,
        severity = ErrorSeverity.ERROR,
        retryable = false,
        context = mapOf(
            "violationCount" to violations.size,
            "fields" to violations.mapNotNull { it.field }.distinct()
        )
    )
) {
    /**
     * Get violations for a specific field
     */
    fun getViolationsFor(field: String): List<ValidationViolation> =
        violations.filter { it.field == field }

    /**
     * Check if a specific field has violations
     */
    fun hasViolationsFor(field: String): Boolean =
        violations.any { it.field == field }

    /**
     * Get all violation messages as strings
     */
    fun getViolationMessages(): List<String> =
        violations.map { violation ->
            if (violation.field != null) {
                "${violation.field}: ${violation.message}"
            } else {
                violation.message
            }
        }
}

/**
 * Concrete validation exception for field-level errors
 */
class FieldValidationException(
    field: String,
    message: String,
    rejectedValue: Any? = null,
    code: String? = null,
    cause: Throwable? = null
) : ValidationException(
    message = "Validation failed for field '$field': $message",
    violations = listOf(ValidationViolation(field, message, rejectedValue, code)),
    cause = cause
)

/**
 * Concrete validation exception for multiple field errors
 */
class MultipleFieldValidationException(
    violations: List<ValidationViolation>,
    cause: Throwable? = null
) : ValidationException(
    message = "Validation failed for ${violations.size} field(s): ${violations.map { it.field }.distinct().joinToString(", ")}",
    violations = violations,
    cause = cause
)

/**
 * Concrete validation exception for general validation errors
 */
class GeneralValidationException(
    message: String,
    cause: Throwable? = null
) : ValidationException(
    message = message,
    violations = listOf(ValidationViolation(null, message)),
    cause = cause
)
