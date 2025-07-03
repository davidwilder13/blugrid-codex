package net.blugrid.api.common.exception

import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException

open class APIException(
    val apiError: APIError,
    val includeStackTrace: Boolean = false
) : HttpStatusException(HttpStatus.valueOf(apiError.statusCode.toInt()), apiError.message) {

    private val stackTraceInfo: List<String> = if (includeStackTrace) {
        this.stackTrace.map { it.toString() }
    } else {
        emptyList()
    }

    fun toResponseBody(): Map<String, Any> {
        val response = mutableMapOf<String, Any>(
            "status" to apiError.statusCode,
            "message" to apiError.message,
            "code" to apiError.code,
            "details" to apiError.details.orEmpty(),
            "_links" to apiError.links.orEmpty()
        )
        apiError.headers?.let { response.putAll(it) }
        if (includeStackTrace && stackTraceInfo.isNotEmpty()) {
            response["stackTrace"] = stackTraceInfo
        }
        return response
    }
}
