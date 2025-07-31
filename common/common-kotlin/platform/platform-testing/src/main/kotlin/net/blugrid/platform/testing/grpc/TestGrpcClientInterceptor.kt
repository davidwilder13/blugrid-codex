package net.blugrid.platform.testing.grpc

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.core.order.Ordered
import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger

/**
 * Test-only gRPC client interceptor that automatically injects test context metadata
 *
 * Mental Model: Similar to TestAuthFilter but for gRPC metadata headers
 * Automatically reads from TestGrpcApplicationContext and injects into every gRPC call
 */
@Requires(env = [Environment.TEST])
@Singleton
class TestGrpcClientInterceptor : ClientInterceptor, Ordered {

    private val log = logger()

    companion object {
        // Same metadata keys as your AuthServerInterceptor expects
        const val SESSION_ID = "x-session-id"
        const val SESSION_TYPE = "x-session-type"
        const val USER_ID = "x-user-id"
        const val TENANT_ID = "x-tenant-id"
        const val BUSINESS_UNIT_ID = "x-business-unit-id"
        const val OPERATOR_ID = "x-operator-id"
        const val WEB_APP_ID = "x-web-app-id"
    }

    // High priority to run early (before your AuthClientInterceptor)
    override fun getOrder(): Int = 50

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {

        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            next.newCall(method, callOptions)
        ) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                // Inject test context if available
                injectTestContext(headers)
                super.start(responseListener, headers)
            }
        }
    }

    /**
     * Inject test context metadata into gRPC headers
     * Similar to how TestAuthFilter adds JWT cookies
     */
    private fun injectTestContext(headers: Metadata) {
        val context = TestGrpcApplicationContext.getCurrentContext()

        if (context != null) {
            log.debug("Injecting gRPC test context: sessionType={}, tenantId={}",
                     context.sessionType, context.tenantId)

            // Add basic context - these are always present
            addHeader(headers, SESSION_ID, context.sessionId)
            addHeader(headers, SESSION_TYPE, context.sessionType)
            addHeader(headers, USER_ID, context.userId)
            addHeader(headers, WEB_APP_ID, context.webAppId)

            // Add optional context based on session type
            context.tenantId?.let { addHeader(headers, TENANT_ID, it) }
            context.operatorId?.let { addHeader(headers, OPERATOR_ID, it) }
            context.businessUnitId?.let { addHeader(headers, BUSINESS_UNIT_ID, it) }

        } else {
            log.debug("No gRPC test context configured - skipping metadata injection")
        }
    }

    private fun addHeader(headers: Metadata, key: String, value: String) {
        headers.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value)
    }
}
