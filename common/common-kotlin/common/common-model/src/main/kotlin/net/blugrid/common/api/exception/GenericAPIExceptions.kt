package net.blugrid.common.api.exception

import io.micronaut.http.HttpStatus
import net.blugrid.common.model.exception.APIClientException
import net.blugrid.common.model.exception.APIError
import net.blugrid.common.model.exception.APIServerException
import net.blugrid.common.model.exception.ResponseStatus

// =============================================================================
// GENERIC API EXCEPTION TYPES - Reusable across all resource types
// =============================================================================

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundAPIException : APIClientException {
    companion object {
        const val CODE = "resource.not.found"
    }

    constructor(resourceType: String, identifier: Any) : super(CODE, resourceType, identifier)
    constructor(error: APIError) : super(error)
}

@ResponseStatus(HttpStatus.CONFLICT)
class ResourceAlreadyExistsAPIException : APIClientException {
    companion object {
        const val CODE = "resource.already.exists"
    }

    constructor(resourceType: String, identifier: Any) : super(CODE, resourceType, identifier)
    constructor(error: APIError) : super(error)
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ResourceValidationAPIException : APIClientException {
    companion object {
        const val CODE = "resource.validation.failed"
    }

    constructor(resourceType: String, field: String, reason: String) : super(CODE, resourceType, field, reason)
    constructor(resourceType: String, violations: List<String>) : super(CODE, resourceType, violations.joinToString(", "))
    constructor(error: APIError) : super(error)
}

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
class TenantContextMissingAPIException : APIClientException {
    companion object {
        const val CODE = "tenant.context.missing"
    }

    constructor(operation: String) : super(CODE, operation)
    constructor(error: APIError) : super(error)

    constructor() : this("unknown")
}

@ResponseStatus(HttpStatus.FORBIDDEN)
class ResourceAccessDeniedAPIException : APIClientException {
    companion object {
        const val CODE = "resource.access.denied"
    }

    constructor(resourceType: String, identifier: Any, action: String) : super(CODE, resourceType, identifier, action)
    constructor(error: APIError) : super(error)
}

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class BusinessRuleViolationAPIException : APIClientException {
    companion object {
        const val CODE = "business.rule.violation"
    }

    constructor(rule: String, resourceType: String?) : super(CODE, rule, resourceType ?: "")
    constructor(rule: String) : this(rule, null)
    constructor(error: APIError) : super(error)
}

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class ResourceConflictAPIException : APIClientException {
    companion object {
        const val CODE = "resource.conflict"
    }

    constructor(resourceType: String, identifier: Any, conflictReason: String) : super(CODE, resourceType, identifier, conflictReason)
    constructor(error: APIError) : super(error)
}

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InternalServerErrorAPIException : APIServerException {
    companion object {
        const val CODE = "internal.server.error"
    }

    constructor(message: String) : super(CODE, message)
    constructor(cause: Throwable) : super(CODE, cause.message ?: "An unexpected error occurred")
    constructor(error: APIError) : super(error)

    // Default constructor for cases where no message is provided
    constructor() : this("An unexpected error occurred")
}
