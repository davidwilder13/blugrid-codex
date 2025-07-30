package net.blugrid.integration.grpc.mapper

import com.google.protobuf.Timestamp
import io.grpc.StatusException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import net.blugrid.api.common.grpc.BusinessRuleError
import net.blugrid.api.common.grpc.ErrorMetadata
import net.blugrid.api.common.grpc.ErrorSeverity
import net.blugrid.api.common.grpc.ResourceError
import net.blugrid.api.common.grpc.Status
import net.blugrid.api.common.grpc.TenantContextError
import net.blugrid.api.common.grpc.ValidationError
import net.blugrid.common.api.exception.BusinessRuleViolationAPIException
import net.blugrid.common.api.exception.InternalServerErrorAPIException
import net.blugrid.common.api.exception.ResourceAccessDeniedAPIException
import net.blugrid.common.api.exception.ResourceAlreadyExistsAPIException
import net.blugrid.common.api.exception.ResourceConflictAPIException
import net.blugrid.common.api.exception.ResourceNotFoundAPIException
import net.blugrid.common.api.exception.ResourceValidationAPIException
import net.blugrid.common.api.exception.TenantContextMissingAPIException
import net.blugrid.common.domain.exception.BusinessRuleException
import net.blugrid.common.domain.exception.DomainException
import net.blugrid.common.domain.exception.InternalServerException
import net.blugrid.common.domain.exception.NotFoundException
import net.blugrid.common.domain.exception.ResourceConflictException
import net.blugrid.common.domain.exception.TenantContextException
import net.blugrid.common.domain.exception.ValidationException
import net.blugrid.common.model.exception.APIClientException
import net.blugrid.common.model.exception.APIException
import net.blugrid.common.model.exception.APIServerException
import net.blugrid.platform.logging.logger
import java.time.Instant
import java.util.UUID
import com.google.protobuf.Any as ProtoAny
import io.grpc.Status as GrpcStatus

@Singleton
class GrpcErrorMapper(
    @Value("\${app.service.name:unknown-service}")
    private val serviceName: String
) {

    private val log = logger()

    /**
     * Maps API exceptions to gRPC StatusException with rich error details
     * Also supports direct mapping from domain exceptions
     */
    fun mapToGrpcException(
        exception: Exception,
        grpcMethod: String,
        correlationId: String? = null
    ): StatusException {

        val apiException = when (exception) {
            is APIException -> exception
            is DomainException -> mapDomainToApiException(exception)
            else -> InternalServerErrorAPIException("Unexpected error: ${exception.message}")
        }

        return mapApiExceptionToGrpc(apiException, grpcMethod, correlationId)
    }

    /**
     * Maps API exceptions to gRPC StatusException with rich error details
     */
    fun mapApiExceptionToGrpc(
        apiException: APIException,
        grpcMethod: String,
        correlationId: String? = null
    ): StatusException {


        val grpcCode = mapToGrpcCode(apiException)
        val errorDetails = createErrorDetails(apiException, grpcMethod, correlationId)

        // Create error metadata and add to details
        val errorMetadata = createErrorMetadata(apiException, grpcMethod, correlationId)
        val allDetails = errorDetails.toMutableList()
        allDetails.add(ProtoAny.pack(errorMetadata))

        // Create enhanced status with details
        val status = Status.newBuilder()
            .setCode(grpcCode.code.value())
            .setMessage(apiException.message ?: getDefaultMessage(apiException))
            .addAllDetails(allDetails)
            .build()

        log.debug(
            "Mapped API exception to gRPC: {} -> {} (method: {})",
            apiException::class.simpleName,
            grpcCode,
            grpcMethod
        )

        return StatusProto.toStatusException(
            com.google.rpc.Status.newBuilder()
                .setCode(grpcCode.code.value())
                .setMessage(status.message)
                .addAllDetails(status.detailsList.map { detail ->
                    ProtoAny.pack(detail)
                })
                .build()
        )
    }

    /**
     * Maps domain exceptions directly to API exceptions
     */
    private fun mapDomainToApiException(domainException: DomainException): APIException {
        return when (domainException) {
            is NotFoundException -> {
                val resourceType = domainException.metadata.context["resourceType"]?.toString() ?: "Resource"
                val identifier = domainException.metadata.context["identifier"] ?: "unknown"
                ResourceNotFoundAPIException(resourceType, identifier)
            }

            is ValidationException -> {
                val resourceType = domainException.metadata.context["resourceType"]?.toString() ?: "Resource"
                val violations = domainException.getViolationMessages()
                ResourceValidationAPIException(resourceType, violations)
            }

            is AccessDeniedException -> {
                val resource = domainException.metadata.context["resource"]?.toString() ?: "Resource"
                val action = domainException.metadata.context["action"]?.toString() ?: "access"
                ResourceAccessDeniedAPIException(resource, "unknown", action)
            }

            is BusinessRuleException -> {
                val rule = domainException.metadata.context["rule"]?.toString() ?: domainException.message ?: "Unknown rule"
                val resourceType = domainException.metadata.context["resourceType"]?.toString()
                BusinessRuleViolationAPIException(rule, resourceType)
            }

            is ResourceConflictException -> {
                val resourceType = domainException.metadata.context["resourceType"]?.toString() ?: "Resource"
                val identifier = domainException.metadata.context["identifier"] ?: "unknown"
                val reason = domainException.message ?: "Resource conflict"
                ResourceConflictAPIException(resourceType, identifier, reason)
            }

            is TenantContextException -> {
                val operation = domainException.metadata.context["operation"]?.toString() ?: "unknown"
                TenantContextMissingAPIException(operation)
            }

            is InternalServerException -> {
                InternalServerErrorAPIException(domainException.message ?: "Internal server error")
            }

            else -> {
                InternalServerErrorAPIException("Unknown domain error: ${domainException.message}")
            }
        }
    }

    /**
     * Maps API exceptions to appropriate gRPC status codes
     */
    private fun mapToGrpcCode(apiException: APIException): GrpcStatus {
        return when (apiException) {
            // Client errors (4xx equivalent)
            is ResourceNotFoundAPIException -> GrpcStatus.NOT_FOUND
            is ResourceValidationAPIException -> GrpcStatus.INVALID_ARGUMENT
            is ResourceAlreadyExistsAPIException -> GrpcStatus.ALREADY_EXISTS
            is ResourceConflictAPIException -> GrpcStatus.ALREADY_EXISTS
            is TenantContextMissingAPIException -> GrpcStatus.FAILED_PRECONDITION
            is BusinessRuleViolationAPIException -> GrpcStatus.FAILED_PRECONDITION
            is ResourceAccessDeniedAPIException -> GrpcStatus.PERMISSION_DENIED

            // Server errors (5xx equivalent)
            is InternalServerErrorAPIException -> GrpcStatus.INTERNAL

            // Default fallback based on exception hierarchy
            else -> when {
                apiException is APIClientException -> GrpcStatus.INVALID_ARGUMENT
                apiException is APIServerException -> GrpcStatus.INTERNAL
                else -> GrpcStatus.UNKNOWN
            }
        }
    }

    /**
     * Creates detailed error information for the gRPC response
     */
    private fun createErrorDetails(
        apiException: APIException,
        grpcMethod: String,
        correlationId: String?
    ): List<ProtoAny> {
        val details = mutableListOf<ProtoAny>()

        when (apiException) {
            is ResourceValidationAPIException -> {
                details.addAll(createValidationErrorDetails(apiException))
            }

            is ResourceNotFoundAPIException,
            is ResourceAlreadyExistsAPIException,
            is ResourceConflictAPIException,
            is ResourceAccessDeniedAPIException -> {
                details.add(createResourceErrorDetail(apiException, grpcMethod))
            }

            is BusinessRuleViolationAPIException -> {
                details.add(createBusinessRuleErrorDetail(apiException))
            }

            is TenantContextMissingAPIException -> {
                details.add(createTenantContextErrorDetail(apiException, grpcMethod))
            }
        }

        return details
    }

    /**
     * Creates validation error details with field-level information
     */
    private fun createValidationErrorDetails(apiException: ResourceValidationAPIException): List<ProtoAny> {
        val details = mutableListOf<ProtoAny>()

        // Try to extract field-level validation errors from the exception message or params
        val violations = extractValidationViolations(apiException)

        if (violations.isNotEmpty()) {
            violations.forEach { violation ->
                val validationError = ValidationError.newBuilder()
                    .setField(violation.field ?: "unknown")
                    .setCode(violation.code ?: "VALIDATION_FAILED")
                    .setMessage(violation.message)
                    .setRejectedValue(violation.rejectedValue?.toString() ?: "")
                    .putAllContext(violation.context)
                    .build()

                details.add(ProtoAny.pack(validationError))
            }
        } else {
            // Fallback to general validation error
            val validationError = ValidationError.newBuilder()
                .setField("unknown")
                .setCode("VALIDATION_FAILED")
                .setMessage(apiException.message ?: "Validation failed")
                .build()

            details.add(ProtoAny.pack(validationError))
        }

        return details
    }

    /**
     * Creates resource error detail
     */
    private fun createResourceErrorDetail(apiException: APIException, grpcMethod: String): ProtoAny {
        val resourceType = apiException.params?.getOrNull(0)?.toString() ?: "Resource"
        val resourceId = apiException.params?.getOrNull(1)?.toString() ?: "unknown"
        val action = when (apiException) {
            is ResourceAccessDeniedAPIException -> apiException.params?.getOrNull(2)?.toString() ?: "access"
            else -> grpcMethod
        }

        val resourceError = ResourceError.newBuilder()
            .setResourceType(resourceType)
            .setResourceId(resourceId)
            .setAction(action)
            .setReason(apiException.message ?: "Resource operation failed")
            .build()

        return ProtoAny.pack(resourceError)
    }

    /**
     * Creates business rule error detail
     */
    private fun createBusinessRuleErrorDetail(apiException: BusinessRuleViolationAPIException): ProtoAny {
        val ruleName = apiException.params?.getOrNull(0)?.toString() ?: "unknown"
        val resourceType = apiException.params?.getOrNull(1)?.toString() ?: ""

        val businessRuleError = BusinessRuleError.newBuilder()
            .setRuleName(ruleName)
            .setRuleDescription(apiException.message ?: "Business rule violated")
            .setResourceType(resourceType)
            .build()

        return ProtoAny.pack(businessRuleError)
    }

    /**
     * Creates tenant context error detail
     */
    private fun createTenantContextErrorDetail(apiException: TenantContextMissingAPIException, grpcMethod: String): ProtoAny {
        val operation = apiException.params?.getOrNull(0)?.toString() ?: grpcMethod

        val tenantError = TenantContextError.newBuilder()
            .setOperation(operation)
            .setRequiredScope("tenant")
            .setCurrentScope("none")
            .build()

        return ProtoAny.pack(tenantError)
    }

    /**
     * Extracts validation violations from the API exception
     */
    private fun extractValidationViolations(apiException: ResourceValidationAPIException): List<GrpcValidationViolation> {
        val violations = mutableListOf<GrpcValidationViolation>()

        apiException.params?.let { params ->
            when {
                params.size >= 3 && params[1] is String && params[2] is String -> {
                    // Single field validation: resourceType, field, reason
                    violations.add(
                        GrpcValidationViolation(
                            field = params[1].toString(),
                            message = params[2].toString(),
                            code = "VALIDATION_FAILED",
                            rejectedValue = null,
                            context = emptyMap()
                        )
                    )
                }

                params.size >= 2 && params[1] is String -> {
                    // Multiple violations as comma-separated string
                    val violationsString = params[1].toString()
                    parseViolationString(violationsString).forEach { violation ->
                        violations.add(violation)
                    }
                }
            }
        }

        return violations
    }

    /**
     * Parses violation strings like "field1: message1, field2: message2"
     */
    private fun parseViolationString(violationsString: String): List<GrpcValidationViolation> {
        return violationsString.split(", ").mapNotNull { violation ->
            val parts = violation.split(": ", limit = 2)
            if (parts.size == 2) {
                GrpcValidationViolation(
                    field = parts[0].trim(),
                    message = parts[1].trim(),
                    code = "VALIDATION_FAILED",
                    rejectedValue = null,
                    context = emptyMap()
                )
            } else {
                GrpcValidationViolation(
                    field = null,
                    message = violation.trim(),
                    code = "VALIDATION_FAILED",
                    rejectedValue = null,
                    context = emptyMap()
                )
            }
        }
    }

    /**
     * Creates error metadata for tracking and debugging
     */
    private fun createErrorMetadata(
        apiException: APIException,
        grpcMethod: String,
        correlationId: String?
    ): ErrorMetadata {
        return ErrorMetadata.newBuilder()
            .setErrorId(UUID.randomUUID().toString())
            .setCorrelationId(correlationId ?: apiException.correlationId ?: UUID.randomUUID().toString())
            .setTimestamp(
                Timestamp.newBuilder()
                    .setSeconds(Instant.now().epochSecond)
                    .setNanos(Instant.now().nano)
                    .build()
            )
            .setSourceService(serviceName)
            .setGrpcMethod(grpcMethod)
            .setRetryable(isRetryable(apiException))
            .setSeverity(mapSeverity(apiException))
            .putAllContext(extractContextMap(apiException))
            .build()
    }

    /**
     * Determines if an exception represents a retryable error
     */
    private fun isRetryable(apiException: APIException): Boolean {
        return when (apiException) {
            // Server errors are potentially retryable
            is InternalServerErrorAPIException -> true

            // Client errors are generally not retryable
            is APIClientException -> false

            // Default to non-retryable for safety
            else -> false
        }
    }

    /**
     * Maps API exception to error severity
     */
    private fun mapSeverity(apiException: APIException): ErrorSeverity {
        return when (apiException) {
            is ResourceValidationAPIException,
            is ResourceNotFoundAPIException -> ErrorSeverity.ERROR_SEVERITY_WARNING

            is ResourceAccessDeniedAPIException,
            is TenantContextMissingAPIException,
            is BusinessRuleViolationAPIException -> ErrorSeverity.ERROR_SEVERITY_ERROR

            is InternalServerErrorAPIException -> ErrorSeverity.ERROR_SEVERITY_CRITICAL

            else -> ErrorSeverity.ERROR_SEVERITY_ERROR
        }
    }

    /**
     * Extracts context information from API exception
     */
    private fun extractContextMap(apiException: APIException): Map<String, String> {
        val context = mutableMapOf<String, String>()

        context["exceptionType"] = apiException::class.simpleName ?: "Unknown"
        context["errorCode"] = apiException.code

        apiException.statusCode?.let { context["httpStatus"] = it }
        apiException.params?.let { params ->
            params.forEachIndexed { index, param ->
                context["param$index"] = param.toString()
            }
        }

        return context
    }

    /**
     * Provides default error messages for exceptions without specific messages
     */
    private fun getDefaultMessage(apiException: APIException): String {
        return when (apiException) {
            is ResourceNotFoundAPIException -> "The requested resource was not found"
            is ResourceValidationAPIException -> "The request contains invalid data"
            is ResourceAlreadyExistsAPIException -> "The resource already exists"
            is ResourceConflictAPIException -> "The operation conflicts with the current state"
            is TenantContextMissingAPIException -> "Tenant context is required for this operation"
            is BusinessRuleViolationAPIException -> "The operation violates business rules"
            is ResourceAccessDeniedAPIException -> "Access to the resource is denied"
            is InternalServerErrorAPIException -> "An internal server error occurred"
            else -> "An error occurred while processing the request"
        }
    }

    /**
     * Maps gRPC StatusException back to API exception (for client-side usage)
     */
    fun mapFromGrpcException(statusException: StatusException): APIException {
        return when (statusException.status.code) {
            GrpcStatus.Code.NOT_FOUND ->
                ResourceNotFoundAPIException("Resource", "unknown")

            GrpcStatus.Code.INVALID_ARGUMENT ->
                ResourceValidationAPIException("Resource", "validation", statusException.message ?: "Invalid argument")

            GrpcStatus.Code.ALREADY_EXISTS ->
                ResourceAlreadyExistsAPIException("Resource", "unknown")

            GrpcStatus.Code.PERMISSION_DENIED ->
                ResourceAccessDeniedAPIException("Resource", "unknown", "access")

            GrpcStatus.Code.FAILED_PRECONDITION ->
                TenantContextMissingAPIException("unknown")

            GrpcStatus.Code.INTERNAL ->
                InternalServerErrorAPIException(statusException.message ?: "Internal server error")

            else ->
                InternalServerErrorAPIException("Unknown gRPC error: ${statusException.message}")
        }
    }

    /**
     * Helper data class for parsing validation violations
     */
    private data class GrpcValidationViolation(
        val field: String?,
        val message: String,
        val code: String,
        val rejectedValue: Any?,
        val context: Map<String, String>
    )
}
