package net.blugrid.integration.grpc.service

import io.grpc.StatusException
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.blugrid.integration.grpc.mapper.GrpcErrorMapper
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.context.RequestContext

/**
 * Updated GrpcServiceExecutor with Context-Aware Execution
 */
@Singleton
class GrpcServiceExecutor(
    private val grpcErrorMapper: GrpcErrorMapper,
    @Named("grpcDispatcher") @Inject private val grpcDispatcher: CoroutineDispatcher
) {
    private val log = logger()

    /**
     * Executes a gRPC call with comprehensive error handling and context preservation
     *
     * CRITICAL CHANGE: Uses withContext(grpcDispatcher) to ensure business logic
     * runs on Micronaut-managed threads that inherit security context.
     */
    suspend fun <T> executeCall(
        methodName: String,
        correlationId: String? = null,
        block: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        val effectiveCorrelationId = correlationId ?: generateCorrelationId()

        return try {
            log.debug(
                "Starting gRPC call: {} (correlationId: {}) on thread: {}",
                methodName, effectiveCorrelationId, Thread.currentThread().name
            )

            // CRITICAL FIX: Execute business logic on context-aware dispatcher
            val result = withContext(grpcDispatcher) {
                log.debug("📌 Executing {} on context thread: {}", methodName, Thread.currentThread().name)

                // Debug: Verify context is available
                debugContextAvailability(methodName)

                // Execute the actual business logic
                block()
            }

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
     * Debug helper to verify context availability
     */
    private fun debugContextAvailability(methodName: String) {
        val currentThread = Thread.currentThread().name
        val auth = RequestContext.currentAuthentication

        if (auth != null) {
            log.debug("✅ Context available for {}: {} on {}", methodName, auth.principalName, currentThread)
        } else {
            log.warn("❌ No context available for {} on {}", methodName, currentThread)
        }
    }

    /**
     * Executes a blocking gRPC call (for non-suspending operations)
     * Note: Blocking calls don't need context switching since they run synchronously
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
