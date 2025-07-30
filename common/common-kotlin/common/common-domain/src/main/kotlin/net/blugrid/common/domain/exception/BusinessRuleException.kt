package net.blugrid.common.domain.exception

/**
 * Business rule precondition failed - domain exception
 */
class BusinessRuleException(
    rule: String,
    cause: Throwable? = null,
    context: Map<String, Any> = emptyMap()
) : DomainException(
    message = "Business rule violation: $rule",
    cause = cause,
    metadata = ErrorMetadata(
        code = "BUSINESS_RULE_VIOLATION",
        severity = ErrorSeverity.ERROR,
        retryable = false,
        context = context + mapOf("rule" to rule)
    )
)
