package net.blugrid.common.model.exception

import io.micronaut.http.HttpStatus
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Base API exception class - equivalent to your Spring APIException
 */
open class APIException : RuntimeException {

    val code: String
    var statusCode: String? = null
    var correlationId: String? = null
    var params: Array<out Any>? = null
    var details: List<Any>? = null

    internal constructor(code: String, params: Array<out Any>?) {
        this.code = code
        this.params = params
    }

    internal constructor(code: String, statusCode: Int, params: Array<out Any>?) {
        this.code = code
        this.params = params
        this.statusCode = statusCode.toString()
    }

    internal constructor(message: String, code: String, params: Array<out Any>?) : super(message) {
        this.code = code
        this.params = params
    }

    internal constructor(message: String, code: String, statusCode: Int, params: Array<out Any>?) : super(message) {
        this.code = code
        this.params = params
        this.statusCode = statusCode.toString()
    }

    internal constructor(cause: Throwable, code: String, params: Array<out Any>?) : super(cause) {
        this.code = code
        this.params = params
    }

    internal constructor(cause: Throwable, code: String, statusCode: Int, params: Array<out Any>?) : super(cause) {
        this.code = code
        this.params = params
        this.statusCode = statusCode.toString()
    }

    internal constructor(message: String, cause: Throwable, code: String, params: Array<out Any>?) : super(message, cause) {
        this.code = code
        this.params = params
    }

    constructor(error: APIError) : super(error.message) {
        statusCode = error.status?.toString()
        code = error.type
        correlationId = null // Extract from error if available
        details = null // Convert from error if needed
    }

    override fun toString(): String {
        return "${javaClass.simpleName}: ${message ?: params?.contentToString()}"
    }
}

/**
 * Client error exceptions (4xx status codes)
 */
abstract class APIClientException : APIException {
    protected constructor(code: String, vararg params: Any) : super(code, params)
    protected constructor(error: APIError) : super(error)
}

/**
 * Server error exceptions (5xx status codes)
 */
abstract class APIServerException : APIException {
    protected constructor(code: String, vararg params: Any) : super(code, params)
    protected constructor(error: APIError) : super(error)
}

/**
 * Service unavailable exceptions
 */
abstract class APIServiceUnavailableException : APIServerException {
    protected constructor(code: String, vararg params: Any) : super(code, params)
    protected constructor(error: APIError) : super(error)
}

@Schema(description = "Core error information following RFC 7807")
data class APIError(
    val type: String,

    val title: String? = null,

    val message: String,

    val status: Int? = null,

    val instance: String? = null
) {
    companion object {
        fun validationError(message: String) = APIError(
            type = "validation-error",
            title = "Validation Failed",
            message = message
        )

        fun notFound(resource: String, id: Any) = APIError(
            type = "resource-not-found",
            title = "Resource Not Found",
            message = "$resource with id '$id' was not found"
        )

        fun accessDenied(resource: String = "resource", action: String = "access") = APIError(
            type = "access-denied",
            title = "Access Denied",
            message = "Access denied for action '$action' on $resource"
        )

        fun tenantContextMissing(operation: String) = APIError(
            type = "tenant-context-missing",
            title = "Tenant Context Required",
            message = "No tenant context available for operation: $operation"
        )

        fun systemError(message: String = "An unexpected error occurred") = APIError(
            type = "system-error",
            title = "System Error",
            message = message
        )
    }
}

@Schema(description = "Detailed error information")
data class APIErrorDetail(
    val field: String? = null,

    val code: String? = null,

    val message: String,

    val rejectedValue: Any? = null,

    val context: Map<String, Any>? = null
) {
    companion object {
        fun required(field: String) = APIErrorDetail(
            field = field,
            code = "REQUIRED",
            message = "Field '$field' is required"
        )

        fun invalid(field: String, value: Any?, reason: String) = APIErrorDetail(
            field = field,
            code = "INVALID",
            message = reason,
            rejectedValue = value
        )

        fun tooLong(field: String, maxLength: Int, actualLength: Int) = APIErrorDetail(
            field = field,
            code = "TOO_LONG",
            message = "Field '$field' must not exceed $maxLength characters",
            context = mapOf(
                "maxLength" to maxLength,
                "actualLength" to actualLength
            )
        )
    }
}

@Schema(description = "Complete error response")
data class APIErrorResponse(
    val error: APIError,

    val details: List<APIErrorDetail> = emptyList(),

    val timestamp: String = java.time.Instant.now().toString(),

    val traceId: String = java.util.UUID.randomUUID().toString(),

    val path: String? = null,

    val method: String? = null,

    val stackTrace: List<String>? = null,

    val debug: Map<String, Any>? = null
) {
    val hasDetails: Boolean get() = details.isNotEmpty()
    val errorFields: List<String> get() = details.mapNotNull { it.field }.distinct()

    companion object {
        fun from(
            error: APIError,
            details: List<APIErrorDetail> = emptyList(),
            path: String? = null,
            method: String? = null,
            stackTrace: List<String>? = null,
            debug: Map<String, Any>? = null
        ) = APIErrorResponse(
            error = error,
            details = details,
            path = path,
            method = method,
            stackTrace = stackTrace,
            debug = debug
        )
    }
}

/**
 * Micronaut annotation to specify HTTP status for exceptions
 * Equivalent to Spring's @ResponseStatus
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResponseStatus(val value: HttpStatus)

