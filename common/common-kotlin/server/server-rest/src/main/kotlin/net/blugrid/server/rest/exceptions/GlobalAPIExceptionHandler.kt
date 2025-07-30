package net.blugrid.server.rest.exceptions

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import net.blugrid.common.api.exception.InternalServerErrorAPIException
import net.blugrid.common.api.exception.ResourceAccessDeniedAPIException
import net.blugrid.common.api.exception.ResourceNotFoundAPIException
import net.blugrid.common.api.exception.ResourceValidationAPIException
import net.blugrid.common.api.exception.TenantContextMissingAPIException
import net.blugrid.common.exception.APIExceptionMapper
import net.blugrid.common.model.exception.APIClientException
import net.blugrid.common.model.exception.APIError
import net.blugrid.common.model.exception.APIErrorResponse
import net.blugrid.common.model.exception.APIException
import net.blugrid.common.model.exception.APIServerException
import net.blugrid.common.model.exception.ResponseStatus
import net.blugrid.platform.logging.api.ErrorLogger

@Controller
class GlobalAPIExceptionHandler(
    private val apiExceptionMapper: APIExceptionMapper,
    private val errorLogger: ErrorLogger
) {

    /**
     * Handle all APIException instances and subclasses
     */
    @Error(global = true, exception = APIException::class)
    fun handleAPIException(
        request: HttpRequest<*>,
        exception: APIException
    ): HttpResponse<APIErrorResponse> {
        errorLogger.logError(
            throwable = exception,
            context = "${request.method} ${request.path}",
            metadata = mapOf("errorCode" to exception.code)
        )
        return mapAPIExceptionToHttp(exception, request)
    }

    /**
     * Handle specific client exceptions with more detailed logging
     */
    @Error(global = true, exception = ResourceNotFoundAPIException::class)
    fun handleResourceNotFound(
        request: HttpRequest<*>,
        exception: ResourceNotFoundAPIException
    ): HttpResponse<APIErrorResponse> {
        errorLogger.logWarning(
            message = "Resource not found: ${exception.message}",
            context = "${request.method} ${request.path}",
            metadata = mapOf("errorCode" to exception.code)
        )
        return mapAPIExceptionToHttp(exception, request)
    }

    /**
     * Handle validation exceptions
     */
    @Error(global = true, exception = ResourceValidationAPIException::class)
    fun handleValidationException(
        request: HttpRequest<*>,
        exception: ResourceValidationAPIException
    ): HttpResponse<APIErrorResponse> {
        errorLogger.logError(
            throwable = exception,
            context = "${request.method} ${request.path}",
            metadata = mapOf(
                "errorCode" to exception.code,
                "validationErrors" to (exception.params?.joinToString(", ") ?: "")
            )
        )
        return mapAPIExceptionToHttp(exception, request)
    }

    /**
     * Handle access denied exceptions
     */
    @Error(global = true, exception = ResourceAccessDeniedAPIException::class)
    fun handleAccessDenied(
        request: HttpRequest<*>,
        exception: ResourceAccessDeniedAPIException
    ): HttpResponse<APIErrorResponse> {
        errorLogger.logError(
            throwable = exception,
            context = "${request.method} ${request.path}",
            metadata = mapOf("errorCode" to exception.code)
        )
        return mapAPIExceptionToHttp(exception, request)
    }

    /**
     * Handle server errors with full stack traces
     */
    @Error(global = true, exception = InternalServerErrorAPIException::class)
    fun handleInternalServerError(
        request: HttpRequest<*>,
        exception: InternalServerErrorAPIException
    ): HttpResponse<APIErrorResponse> {
        errorLogger.logError(
            throwable = exception,
            context = "${request.method} ${request.path}",
            metadata = mapOf("errorCode" to exception.code)
        )
        return mapAPIExceptionToHttp(exception, request)
    }

    /**
     * Fallback handler for any other exceptions
     */
    @Error(global = true, exception = Exception::class)
    fun handleGenericException(
        request: HttpRequest<*>,
        exception: Exception
    ): HttpResponse<APIErrorResponse> {
        val apiException = apiExceptionMapper.mapToAPIException(exception, "unknown")

        errorLogger.logError(
            throwable = exception,
            context = "${request.method} ${request.path}",
            metadata = mapOf(
                "originalType" to exception.javaClass.simpleName,
                "mappedCode" to apiException.code
            )
        )

        return mapAPIExceptionToHttp(apiException, request)
    }

    /**
     * Maps APIException to HTTP response using the injected mapper
     */
    private fun mapAPIExceptionToHttp(
        exception: APIException,
        request: HttpRequest<*>
    ): HttpResponse<APIErrorResponse> {
        val httpStatus = determineHttpStatus(exception)
        val error = mapToAPIError(exception)

        val errorResponse = APIErrorResponse.from(
            error = error,
            path = request.path,
            method = request.method.name,
            stackTrace = if (includeStackTrace()) {
                exception.stackTrace.map { it.toString() }
            } else null
        )

        return HttpResponse.status<APIErrorResponse>(httpStatus).body(errorResponse)
    }

    private fun determineHttpStatus(exception: APIException): HttpStatus {
        // Use reflection to check for @ResponseStatus annotation
        val responseStatus = exception.javaClass.getAnnotation(ResponseStatus::class.java)
        return responseStatus?.value ?: when (exception) {
            is APIClientException -> HttpStatus.BAD_REQUEST
            is APIServerException -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }

    private fun mapToAPIError(exception: APIException): APIError {
        return when (exception) {
            is ResourceNotFoundAPIException -> APIError.notFound(
                resource = exception.params?.getOrNull(0)?.toString() ?: "resource",
                id = exception.params?.getOrNull(1) ?: "unknown"
            )

            is ResourceAccessDeniedAPIException -> APIError.accessDenied(
                resource = exception.params?.getOrNull(0)?.toString() ?: "resource",
                action = exception.params?.getOrNull(2)?.toString() ?: "access"
            )

            is TenantContextMissingAPIException -> APIError.tenantContextMissing(
                operation = exception.params?.getOrNull(0)?.toString() ?: "unknown"
            )

            is ResourceValidationAPIException -> APIError.validationError(
                message = exception.message ?: "Validation failed"
            )

            else -> APIError.systemError(exception.message ?: "An unexpected error occurred")
        }
    }

    private fun includeStackTrace(): Boolean {
        // Control via system property or environment
        return System.getProperty("micronaut.env") != "prod"
    }
}
