package net.blugrid.api.config

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import net.blugrid.api.common.exception.APIException
import net.blugrid.api.logging.logger

@Produces
@Singleton
@Requires(classes = [APIException::class, ExceptionHandler::class])
class GlobalExceptionHandler : ExceptionHandler<Throwable, HttpResponse<Map<String, Any>>> {

    private val log = logger()

    override fun handle(request: HttpRequest<*>, exception: Throwable): HttpResponse<Map<String, Any>> {
        log.error("Exception occurred: ${exception.localizedMessage}")

        if (exception is APIException) {
            log.error("Error Code: ${exception.apiError.code}")

            // Log stack trace if required
            if (exception.includeStackTrace) {
                exception.stackTrace.forEach {
                    log.error(it.toString())
                }
            }

            return HttpResponse.status<Map<String, Any>>(exception.status)
                .body(exception.toResponseBody())
        } else {
            // Handle other types of exceptions (e.g., unexpected errors)
            log.error("Unexpected error: ${exception.message}")
            exception.stackTrace.forEach {
                log.error(it.toString())
            }

            // Return a generic internal server error response
            return HttpResponse.status<Map<String, Any>>(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf(
                    "status" to HttpStatus.INTERNAL_SERVER_ERROR.code,
                    "message" to "An unexpected error occurred",
                    "code" to "INTERNAL_SERVER_ERROR"
                ))
        }
    }
}
