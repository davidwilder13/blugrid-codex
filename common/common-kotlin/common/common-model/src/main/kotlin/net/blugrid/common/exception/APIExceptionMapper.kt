package net.blugrid.common.exception

import jakarta.inject.Singleton
import net.blugrid.common.api.exception.BusinessRuleViolationAPIException
import net.blugrid.common.api.exception.InternalServerErrorAPIException
import net.blugrid.common.api.exception.ResourceAccessDeniedAPIException
import net.blugrid.common.api.exception.ResourceConflictAPIException
import net.blugrid.common.api.exception.ResourceNotFoundAPIException
import net.blugrid.common.api.exception.ResourceValidationAPIException
import net.blugrid.common.api.exception.TenantContextMissingAPIException
import net.blugrid.common.domain.exception.AccessDeniedException
import net.blugrid.common.domain.exception.BusinessRuleException
import net.blugrid.common.domain.exception.InternalServerException
import net.blugrid.common.domain.exception.NotFoundException
import net.blugrid.common.domain.exception.ResourceConflictException
import net.blugrid.common.domain.exception.TenantContextException
import net.blugrid.common.domain.exception.ValidationException
import net.blugrid.common.model.exception.APIException

@Singleton
class APIExceptionMapper {

    /**
     * Maps any throwable to an API exception using reflection-based detection
     */
    fun mapToAPIException(throwable: Throwable, operation: String? = null): APIException {
        return when {
            // Already an API exception - pass through
            throwable is APIException -> throwable

            // Map specific domain exceptions to API exceptions
            throwable is NotFoundException -> mapNotFoundException(throwable)
            throwable is ValidationException -> mapValidationException(throwable)
            throwable is TenantContextException -> mapTenantContextException(throwable, operation)
            throwable is AccessDeniedException -> mapAccessDeniedException(throwable)
            throwable is BusinessRuleException -> mapBusinessRuleViolation(throwable)
            throwable is ResourceConflictException -> mapResourceConflict(throwable)
            throwable is InternalServerException -> mapInternalServerException(throwable)

            // Map by class name using reflection for framework exceptions
            else -> mapByClassName(throwable, operation)
        }
    }

    private fun mapNotFoundException(ex: NotFoundException): APIException {
        // Extract resource type and ID from context
        val resourceType = ex.metadata.context["resourceType"] as? String ?: "Resource"
        val identifier = ex.metadata.context["identifier"] ?: "unknown"

        return ResourceNotFoundAPIException(resourceType, identifier)
    }

    private fun mapValidationException(ex: ValidationException): APIException {
        // Extract resource type from context if available
        val resourceType = ex.metadata.context["resourceType"] as? String ?: "Resource"

        return when {
            ex.violations.isNotEmpty() -> {
                // Multiple violations - extract violation messages
                val violationMessages = ex.violations.map { violation ->
                    violation.message
                }
                ResourceValidationAPIException(resourceType, violationMessages)
            }

            else -> {
                // Single violation or general message
                ResourceValidationAPIException(resourceType, "unknown", ex.message ?: "Validation failed")
            }
        }
    }

    private fun mapTenantContextException(ex: TenantContextException, operation: String?): APIException {
        val operationName = operation ?: ex.metadata.context["operation"]?.toString() ?: "unknown"
        return TenantContextMissingAPIException(operationName)
    }

    private fun mapAccessDeniedException(ex: AccessDeniedException): APIException {
        val resourceType = ex.metadata.context["resource"]?.toString() ?: "Resource"
        val identifier = ex.metadata.context["identifier"] ?: "unknown"
        val action = ex.metadata.context["action"]?.toString() ?: "access"
        return ResourceAccessDeniedAPIException(resourceType, identifier, action)
    }

    private fun mapBusinessRuleViolation(ex: BusinessRuleException): APIException {
        val rule = ex.metadata.context["rule"]?.toString() ?: ex.message ?: "Unknown rule"
        val resourceType = ex.metadata.context["resourceType"]?.toString()
        return BusinessRuleViolationAPIException(rule, resourceType)
    }

    private fun mapResourceConflict(ex: ResourceConflictException): APIException {
        val resourceType = ex.metadata.context["resourceType"]?.toString() ?: "Resource"
        val identifier = ex.metadata.context["identifier"] ?: "unknown"
        val reason = ex.message ?: "Resource conflict"
        return ResourceConflictAPIException(resourceType, identifier, reason)
    }

    private fun mapInternalServerException(ex: InternalServerException): APIException {
        return InternalServerErrorAPIException(ex.message ?: "Internal server error")
    }

    /**
     * Maps framework exceptions by class name reflection
     */
    private fun mapByClassName(throwable: Throwable, operation: String?): APIException {
        val className = throwable::class.simpleName ?: "Unknown"
        val qualifiedName = throwable::class.qualifiedName ?: ""

        return when {
            // Jakarta/Hibernate validation exceptions
            isValidationException(qualifiedName) -> {
                mapValidationFrameworkException(throwable, operation)
            }

            // JPA/Hibernate persistence exceptions
            isPersistenceException(qualifiedName) -> {
                mapPersistenceFrameworkException(throwable, operation)
            }

            // Security exceptions
            isSecurityException(qualifiedName) -> {
                mapSecurityFrameworkException(throwable, operation)
            }

            // HTTP client exceptions
            isHttpClientException(qualifiedName) -> {
                mapHttpClientFrameworkException(throwable, operation)
            }

            // Catch-all for unknown exceptions
            else -> {
                InternalServerErrorAPIException(
                    throwable.message ?: "Unknown internal error${operation?.let { " during $it" } ?: ""}"
                )
            }
        }
    }

    // Framework exception detection helpers
    private fun isValidationException(qualifiedName: String): Boolean {
        return qualifiedName.contains("validation", ignoreCase = true) ||
                qualifiedName.contains("ConstraintViolation", ignoreCase = true) ||
                qualifiedName.contains("jakarta.validation") ||
                qualifiedName.contains("javax.validation")
    }

    private fun isPersistenceException(qualifiedName: String): Boolean {
        return qualifiedName.contains("persistence", ignoreCase = true) ||
                qualifiedName.contains("hibernate", ignoreCase = true) ||
                qualifiedName.contains("jpa", ignoreCase = true) ||
                qualifiedName.contains("DataIntegrityViolation") ||
                qualifiedName.contains("SQLException")
    }

    private fun isSecurityException(qualifiedName: String): Boolean {
        return qualifiedName.contains("security", ignoreCase = true) ||
                qualifiedName.contains("authentication", ignoreCase = true) ||
                qualifiedName.contains("authorization", ignoreCase = true) ||
                qualifiedName.contains("AccessDenied")
    }

    private fun isHttpClientException(qualifiedName: String): Boolean {
        return qualifiedName.contains("HttpClient") ||
                qualifiedName.contains("micronaut.http") ||
                qualifiedName.contains("ConnectException") ||
                qualifiedName.contains("TimeoutException")
    }

    // Framework exception mappers
    private fun mapValidationFrameworkException(throwable: Throwable, operation: String?): APIException {
        val message = throwable.message ?: "Validation failed"
        return ResourceValidationAPIException("Resource", "validation", message)
    }

    private fun mapPersistenceFrameworkException(throwable: Throwable, operation: String?): APIException {
        val message = throwable.message ?: "Database operation failed"

        return when {
            // Constraint violations typically indicate conflicts
            message.contains("constraint", ignoreCase = true) ||
                    message.contains("duplicate", ignoreCase = true) ||
                    message.contains("unique", ignoreCase = true) -> {
                ResourceConflictAPIException("Resource", "unknown", "Database constraint violation")
            }

            // Not found scenarios
            message.contains("not found", ignoreCase = true) -> {
                ResourceNotFoundAPIException("Resource", "unknown")
            }

            // Default to internal server error for other persistence issues
            else -> {
                InternalServerErrorAPIException("Database operation failed: ${throwable.message}")
            }
        }
    }

    private fun mapSecurityFrameworkException(throwable: Throwable, operation: String?): APIException {
        return ResourceAccessDeniedAPIException("Resource", "unknown", operation ?: "access")
    }

    private fun mapHttpClientFrameworkException(throwable: Throwable, operation: String?): APIException {
        val message = throwable.message ?: "External service communication failed"
        return InternalServerErrorAPIException("Service communication error: $message")
    }
}
