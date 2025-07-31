package net.blugrid.security.core.context

import io.micronaut.http.HttpRequest
import io.micronaut.http.context.ServerRequestContext
import io.micronaut.security.filters.SecurityFilter

/**
 * DRY utilities for managing request context in both HTTP and gRPC scenarios
 *
 * Mental Model: Universal request context manager that works for both protocols
 */
object RequestContextUtils {

    /**
     * Execute action within a request context, creating one if necessary
     *
     * @param requestSetup Optional function to configure the request before execution
     * @param action The action to execute within the context
     */
    fun <T> doInRequestContext(
        requestSetup: ((HttpRequest<Any>) -> Unit)? = null,
        action: () -> T
    ): T {
        val currentRequest = ServerRequestContext.currentRequest<Any>()

        return if (currentRequest.isPresent) {
            // HTTP request context exists - configure and use it
            val request = currentRequest.get()
            requestSetup?.invoke(request)
            action()
        } else {
            // No HTTP context (typical for gRPC) - create, configure, and maintain it
            val newRequest = createNewRequest()
            requestSetup?.invoke(newRequest)

            executeInContext(newRequest, action)
        }
    }

    /**
     * Execute action within an authenticated request context
     */
    fun <T> doInAuthenticatedRequestContext(
        authenticationJson: String,
        action: () -> T
    ): T = doInRequestContext(
        requestSetup = { request ->
            request.setAttribute(SecurityFilter.AUTHENTICATION, authenticationJson)
        },
        action = action
    )

    /**
     * Execute action within a request context with custom attributes
     */
    fun <T> doInRequestContextWithAttributes(
        attributes: Map<String, Any>,
        action: () -> T
    ): T = doInRequestContext(
        requestSetup = { request ->
            attributes.forEach { (key, value) ->
                request.setAttribute(key, value)
            }
        },
        action = action
    )

    /**
     * Helper to execute action within a specific request context
     */
    private fun <T> executeInContext(
        request: HttpRequest<Any>,
        action: () -> T
    ): T {
        var result: T? = null
        var exception: Throwable? = null

        ServerRequestContext.with(request) {
            try {
                result = action()
            } catch (e: Throwable) {
                exception = e
            }
        }

        // Re-throw any exception that occurred
        exception?.let { throw it }

        return result ?: throw IllegalStateException("Action returned null")
    }

    /**
     * Create a minimal HTTP request for context purposes
     */
    fun createNewRequest(): HttpRequest<Any> {
        // Create a proper HTTP request that can hold attributes using Micronaut's API
        return HttpRequest.GET<Any>("/test-context").apply {
            // Set default headers that might be expected
            headers.add("Content-Type", "application/json")
            headers.add("User-Agent", "test-client")
        }
    }
}

/**
 * Convenience extension functions for cleaner usage
 */
fun <T> doInRequestContext(action: () -> T): T {
    return RequestContextUtils.doInRequestContext(action = action)
}

/**
 * DSL-style builder for complex request context setup
 */
class RequestContextBuilder {
    private val attributes = mutableMapOf<String, Any>()

    fun setAttribute(key: String, value: Any) {
        attributes[key] = value
    }

    fun setAuthentication(authenticationJson: String) {
        setAttribute(SecurityFilter.AUTHENTICATION.toString(), authenticationJson)
    }

    fun <T> execute(action: () -> T): T {
        return RequestContextUtils.doInRequestContextWithAttributes(
            attributes = attributes,
            action = action
        )
    }
}

fun createNewHttpRequest(): HttpRequest<Any> {
    return RequestContextUtils.createNewRequest()
}

/**
 * DSL function for complex request context setup
 *
 * Usage:
 * ```
 * val result = requestContext {
 *     setAuthentication(authJson)
 *     setAttribute("customKey", "customValue")
 * }.execute {
 *     // Your action here
 * }
 * ```
 */
fun requestContext(setup: RequestContextBuilder.() -> Unit): RequestContextBuilder {
    return RequestContextBuilder().apply(setup)
}
