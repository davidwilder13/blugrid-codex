package net.blugrid.common.domain.exception

/**
 * Factory for creating domain exceptions with rich context
 */
object DomainExceptions {

    // Resource not found
    fun notFound(resourceType: String, identifier: Any, cause: Throwable? = null) =
        NotFoundException(resourceType, identifier, cause)

    // Validation errors
    fun fieldValidation(field: String, message: String, rejectedValue: Any? = null, code: String? = null) =
        FieldValidationException(field, message, rejectedValue, code)

    fun multipleFieldValidation(violations: List<ValidationViolation>) =
        MultipleFieldValidationException(violations)

    fun generalValidation(message: String, cause: Throwable? = null) =
        GeneralValidationException(message, cause)

    // Access control
    fun accessDenied(resource: String = "resource", action: String = "access", cause: Throwable? = null) =
        AccessDeniedException(resource, action, cause)

    // Business rules
    fun businessRule(rule: String, cause: Throwable? = null, context: Map<String, Any> = emptyMap()) =
        BusinessRuleException(rule, cause, context)

    // Resource conflicts
    fun resourceConflict(
        resourceType: String,
        identifier: Any,
        conflictReason: String = "Resource conflict",
        cause: Throwable? = null
    ) = ResourceConflictException(resourceType, identifier, conflictReason, cause)

    // Tenant context
    fun tenantContextMissing(
        message: String = "No tenant context available for operation",
        cause: Throwable? = null,
        correlationId: String? = null
    ) = TenantContextException(message, cause, correlationId)

    // Internal server errors
    fun internalServer(message: String = "An unexpected error occurred", cause: Throwable? = null) =
        InternalServerException(message, cause)
}

/**
 * Extension functions for easier exception creation with context
 */

// String extensions for resource types
fun String.notFound(identifier: Any, cause: Throwable? = null) =
    DomainExceptions.notFound(this, identifier, cause)

fun String.alreadyExists(identifier: Any, cause: Throwable? = null) =
    DomainExceptions.resourceConflict(this, identifier, "Resource already exists", cause)

fun String.conflictWith(identifier: Any, reason: String, cause: Throwable? = null) =
    DomainExceptions.resourceConflict(this, identifier, reason, cause)

// Validation builder
class ValidationBuilder {
    private val violations = mutableListOf<ValidationViolation>()

    fun field(field: String, message: String, rejectedValue: Any? = null, code: String? = null): ValidationBuilder {
        violations.add(ValidationViolation(field, message, rejectedValue, code))
        return this
    }

    fun required(field: String, rejectedValue: Any? = null): ValidationBuilder {
        violations.add(ValidationViolation(field, "Field '$field' is required", rejectedValue, "REQUIRED"))
        return this
    }

    fun invalid(field: String, reason: String, rejectedValue: Any? = null): ValidationBuilder {
        violations.add(ValidationViolation(field, reason, rejectedValue, "INVALID"))
        return this
    }

    fun tooLong(field: String, maxLength: Int, actualLength: Int, rejectedValue: Any? = null): ValidationBuilder {
        violations.add(
            ValidationViolation(
                field,
                "Field '$field' must not exceed $maxLength characters (actual: $actualLength)",
                rejectedValue,
                "TOO_LONG"
            )
        )
        return this
    }

    fun tooShort(field: String, minLength: Int, actualLength: Int, rejectedValue: Any? = null): ValidationBuilder {
        violations.add(
            ValidationViolation(
                field,
                "Field '$field' must be at least $minLength characters (actual: $actualLength)",
                rejectedValue,
                "TOO_SHORT"
            )
        )
        return this
    }

    fun build(): ValidationException {
        return when (violations.size) {
            0 -> throw IllegalStateException("At least one validation violation is required")
            1 -> {
                val violation = violations.first()
                FieldValidationException(
                    violation.field ?: "unknown",
                    violation.message,
                    violation.rejectedValue,
                    violation.code
                )
            }
            else -> MultipleFieldValidationException(violations.toList())
        }
    }
}

/**
 * DSL for building validation exceptions
 */
fun validation(block: ValidationBuilder.() -> Unit): ValidationException {
    return ValidationBuilder().apply(block).build()
}

/**
 * Context-aware exception builders
 */
class DomainExceptionContext(
    private val resourceType: String? = null,
    private val identifier: Any? = null,
    private val operation: String? = null,
    private val correlationId: String? = null
) {

    fun notFound(cause: Throwable? = null) =
        NotFoundException(resourceType ?: "Resource", identifier ?: "unknown", cause)

    fun accessDenied(action: String = operation ?: "access", cause: Throwable? = null) =
        AccessDeniedException(resourceType ?: "resource", action, cause)

    fun conflict(reason: String = "Resource conflict", cause: Throwable? = null) =
        ResourceConflictException(resourceType ?: "Resource", identifier ?: "unknown", reason, cause)

    fun validation(block: ValidationBuilder.() -> Unit): ValidationException {
        val validationException = validation(block)
        // Add resource context to validation exception metadata
        val contextualMetadata = validationException.metadata.copy(
            context = validationException.metadata.context + mapOfNotNull(
                "resourceType" to resourceType,
                "identifier" to identifier?.toString(),
                "operation" to operation,
                "correlationId" to correlationId
            )
        )

        // Create new exception with enriched context
        return when (validationException) {
            is FieldValidationException -> FieldValidationException(
                validationException.violations.first().field ?: "unknown",
                validationException.violations.first().message,
                validationException.violations.first().rejectedValue,
                validationException.violations.first().code,
                validationException.cause
            )
            is MultipleFieldValidationException -> MultipleFieldValidationException(
                validationException.violations,
                validationException.cause
            )
            else -> validationException
        }
    }
}

/**
 * Creates a context for domain exception building
 */
fun domainContext(
    resourceType: String? = null,
    identifier: Any? = null,
    operation: String? = null,
    correlationId: String? = null
) = DomainExceptionContext(resourceType, identifier, operation, correlationId)

/**
 * Helper to create map without null values
 */
private fun mapOfNotNull(vararg pairs: Pair<String, Any?>): Map<String, Any> {
    return pairs.filter { it.second != null }.associate { it.first to it.second!! }
}
