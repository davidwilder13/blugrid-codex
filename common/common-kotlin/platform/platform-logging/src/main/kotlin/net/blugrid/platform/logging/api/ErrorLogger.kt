package net.blugrid.platform.logging.api

import jakarta.inject.Singleton
import net.blugrid.common.domain.exception.DomainException
import net.blugrid.common.model.exception.APIException
import net.blugrid.platform.logging.logger

@Singleton
class ErrorLogger {

    private val log = logger()

    /**
     * Log error with structured context - main entry point
     */
    fun logError(
        throwable: Throwable,
        context: String,
        operation: String? = null,
        metadata: Map<String, Any?> = emptyMap()
    ) {
        try {
            val logContext = buildLogContext(throwable, context, operation, metadata)

            // Format the main log message
            val message = buildLogMessage(throwable, context, operation)

            // Log with structured context
            log.error(message, logContext, throwable)

        } catch (e: Exception) {
            // Fallback logging if structured logging fails
            log.error(
                "Failed to log error properly. Original error: {} - {}",
                throwable::class.simpleName, throwable.message, throwable
            )
            log.error("Logging error: {}", e.message, e)
        }
    }

    /**
     * Log warning with structured context - for non-critical errors
     */
    fun logWarning(
        message: String,
        context: String,
        operation: String? = null,
        metadata: Map<String, Any?> = emptyMap(),
        throwable: Throwable? = null
    ) {
        try {
            val logContext = buildWarningLogContext(context, operation, metadata, throwable)

            // Format the main log message
            val logMessage = buildWarningLogMessage(message, context, operation)

            // Log with structured context - include throwable if provided
            if (throwable != null) {
                log.warn(logMessage, logContext, throwable)
            } else {
                log.warn(logMessage, logContext)
            }

        } catch (e: Exception) {
            // Fallback logging if structured logging fails
            log.warn("Failed to log warning properly. Original message: {}", message)
            log.error("Warning logging error: {}", e.message, e)
        }
    }

    /**
     * Build structured logging context for errors
     */
    private fun buildLogContext(
        throwable: Throwable,
        context: String,
        operation: String?,
        metadata: Map<String, Any?>
    ): Map<String, Any?> {
        val logContext = mutableMapOf<String, Any?>(
            "context" to context,
            "exceptionType" to throwable::class.simpleName,
            "exceptionClass" to throwable::class.qualifiedName
        )

        // Add operation if provided
        operation?.let { logContext["operation"] = it }

        // Add custom metadata
        logContext.putAll(metadata)

        // Extract information from different exception types
        when (throwable) {
            is APIException -> {
                logContext["errorCode"] = throwable.code
                logContext["apiStatusCode"] = throwable.statusCode
                logContext["correlationId"] = throwable.correlationId
                logContext["params"] = throwable.params?.contentToString()
            }

            is DomainException -> {
                logContext["errorCode"] = throwable.metadata.code
                logContext["severity"] = throwable.metadata.severity.name
                logContext["retryable"] = throwable.metadata.retryable
                logContext["correlationId"] = throwable.metadata.correlationId
                logContext["traceId"] = throwable.metadata.traceId

                // Add domain context (filter out null values)
                throwable.metadata.context
                    .filterValues { it != null }
                    .forEach { (key, value) -> logContext["domain.$key"] = value }
            }
        }

        // Remove null values for cleaner logs
        return logContext.filterValues { it != null }
    }

    /**
     * Build structured logging context for warnings
     */
    private fun buildWarningLogContext(
        context: String,
        operation: String?,
        metadata: Map<String, Any?>,
        throwable: Throwable?
    ): Map<String, Any?> {
        val logContext = mutableMapOf<String, Any?>(
            "context" to context,
            "level" to "WARNING"
        )

        // Add operation if provided
        operation?.let { logContext["operation"] = it }

        // Add custom metadata
        logContext.putAll(metadata)

        // If throwable is provided, extract relevant information
        throwable?.let { t ->
            logContext["exceptionType"] = t::class.simpleName
            logContext["exceptionClass"] = t::class.qualifiedName

            when (t) {
                is APIException -> {
                    logContext["errorCode"] = t.code
                    logContext["apiStatusCode"] = t.statusCode
                    logContext["correlationId"] = t.correlationId
                    logContext["params"] = t.params?.contentToString()
                }

                is DomainException -> {
                    logContext["errorCode"] = t.metadata.code
                    logContext["severity"] = t.metadata.severity.name
                    logContext["retryable"] = t.metadata.retryable
                    logContext["correlationId"] = t.metadata.correlationId
                    logContext["traceId"] = t.metadata.traceId

                    // Add domain context (filter out null values)
                    t.metadata.context
                        .filterValues { it != null }
                        .forEach { (key, value) -> logContext["domain.$key"] = value }
                }
            }
        }

        // Remove null values for cleaner logs
        return logContext.filterValues { it != null }
    }

    /**
     * Build a human-readable log message for errors
     */
    private fun buildLogMessage(
        throwable: Throwable,
        context: String,
        operation: String?
    ): String {
        val baseMessage = "$context - ${throwable::class.simpleName}: ${throwable.message ?: "Unknown error"}"

        return if (operation != null) {
            "$baseMessage (operation: $operation)"
        } else {
            baseMessage
        }
    }

    /**
     * Build a human-readable warning log message
     */
    private fun buildWarningLogMessage(
        message: String,
        context: String,
        operation: String?
    ): String {
        val baseMessage = "$context - $message"

        return if (operation != null) {
            "$baseMessage (operation: $operation)"
        } else {
            baseMessage
        }
    }
}
