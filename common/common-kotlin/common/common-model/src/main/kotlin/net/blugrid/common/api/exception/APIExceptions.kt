package net.blugrid.common.api.exception

// Helper factory for common patterns
object APIExceptions {

    // Resource operations
    fun notFound(resourceType: String, id: Any) =
        ResourceNotFoundAPIException(resourceType, id)

    fun alreadyExists(resourceType: String, identifier: Any) =
        ResourceAlreadyExistsAPIException(resourceType, identifier)

    fun validationFailed(resourceType: String, field: String, reason: String) =
        ResourceValidationAPIException(resourceType, field, reason)

    fun validationFailed(resourceType: String, violations: List<String>) =
        ResourceValidationAPIException(resourceType, violations)

    fun accessDenied(resourceType: String, id: Any, action: String = "access") =
        ResourceAccessDeniedAPIException(resourceType, id, action)

    fun businessRule(rule: String, resourceType: String? = null) =
        BusinessRuleViolationAPIException(rule, resourceType)

    fun businessRule(rule: String) =
        BusinessRuleViolationAPIException(rule)

    fun conflict(resourceType: String, identifier: Any, reason: String) =
        ResourceConflictAPIException(resourceType, identifier, reason)

    // Context operations
    fun tenantContextMissing(operation: String) =
        TenantContextMissingAPIException(operation)

    fun tenantContextMissing() =
        TenantContextMissingAPIException()

    // System errors
    fun internalError(message: String) =
        InternalServerErrorAPIException(message)

    fun internalError(cause: Throwable) =
        InternalServerErrorAPIException(cause)

    fun internalError() =
        InternalServerErrorAPIException()
}
