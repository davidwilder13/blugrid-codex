package net.blugrid.integration.grpc.service

import io.grpc.StatusException
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import net.blugrid.integration.grpc.mapper.GrpcErrorMapper
import net.blugrid.platform.logging.logger

/**
 * Executor for standardized gRPC operations with error handling and logging.
 * Uses composition pattern to be injectable into any gRPC service.
 */
@Singleton
class GrpcServiceExecutor(
    private val grpcErrorMapper: GrpcErrorMapper
) {
    private val log = logger()

    @Value("\${app.service.name:unknown-service}")
    private lateinit var serviceName: String

    /**
     * Executes a gRPC call with comprehensive error handling and logging
     */
    suspend fun <T> executeCall(
        methodName: String,
        correlationId: String? = null,
        block: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        val effectiveCorrelationId = correlationId ?: generateCorrelationId()

        return try {
            log.debug("Starting gRPC call: {} (correlationId: {})", methodName, effectiveCorrelationId)
            val result = block()
            val duration = System.currentTimeMillis() - startTime
            log.debug("Completed gRPC call: {} in {}ms", methodName, duration)
            result
        } catch (e: StatusException) {
            log.debug("Re-throwing existing gRPC exception for {}: {}", methodName, e.message)
            throw e
        } catch (e: Exception) {
            log.error("Exception in gRPC call {}: {}", methodName, e.message, e)
            throw grpcErrorMapper.mapToGrpcException(
                exception = e,
                grpcMethod = methodName,
                correlationId = effectiveCorrelationId
            )
        }
    }

    /**
     * Executes a blocking gRPC call (for non-suspending operations)
     */
    fun <T> executeCallBlocking(
        methodName: String,
        correlationId: String? = null,
        block: () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        val effectiveCorrelationId = correlationId ?: generateCorrelationId()

        return try {
            log.debug("Starting blocking gRPC call: {} (correlationId: {})", methodName, effectiveCorrelationId)
            val result = block()
            val duration = System.currentTimeMillis() - startTime
            log.debug("Completed blocking gRPC call: {} in {}ms", methodName, duration)
            result
        } catch (e: StatusException) {
            log.debug("Re-throwing existing gRPC exception for {}: {}", methodName, e.message)
            throw e
        } catch (e: Exception) {
            log.error("Exception in blocking gRPC call {}: {}", methodName, e.message, e)
            throw grpcErrorMapper.mapToGrpcException(
                exception = e,
                grpcMethod = methodName,
                correlationId = effectiveCorrelationId
            )
        }
    }

    private fun generateCorrelationId(): String {
        return "grpc-${System.currentTimeMillis()}-${(1000..9999).random()}"
    }
}
