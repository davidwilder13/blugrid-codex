package net.blugrid.integration.grpc.service

import jakarta.inject.Inject

/**
 * Abstract base class for gRPC services that need to extend a specific base.
 * Use this when composition isn't preferred.
 */
interface GrpcService {
    val grpcExecutor: GrpcServiceExecutor

    /**
     * Execute a suspending gRPC operation with error handling
     */
    suspend fun <T> grpcCall(
        methodName: String,
        correlationId: String? = null,
        block: suspend () -> T
    ): T = grpcExecutor.executeCall(methodName, correlationId, block)

    /**
     * Execute a blocking gRPC operation with error handling
     */
    fun <T> grpcCallBlocking(
        methodName: String,
        correlationId: String? = null,
        block: () -> T
    ): T = grpcExecutor.executeCallBlocking(methodName, correlationId, block)
}
