package net.blugrid.integration.grpc.client.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.context.CurrentRequestContext
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.TenantSession

/**
 * Minimal client interceptor that only transports authentication context
 * No validation, no error handling - just pure context forwarding
 *
 * Mental Model: Simple envelope that packages current context and sends it along
 */
@Singleton
class AuthClientInterceptor : ClientInterceptor {

    private val log = logger()

    companion object {
        // Simple metadata keys for internal context transport
        const val SESSION_ID = "x-session-id"
        const val SESSION_TYPE = "x-session-type"
        const val USER_ID = "x-user-id"
        const val TENANT_ID = "x-tenant-id"
        const val BUSINESS_UNIT_ID = "x-business-unit-id"
        const val OPERATOR_ID = "x-operator-id"
        const val WEB_APP_ID = "x-web-app-id"
    }

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {

        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            next.newCall(method, callOptions)
        ) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                // Extract and forward current context - no validation
                addContextToHeaders(headers)
                super.start(responseListener, headers)
            }
        }
    }

    /**
     * Extract current context and add to gRPC headers
     * Simple, fast, no error handling
     */
    private fun addContextToHeaders(headers: Metadata) {
        val session = CurrentRequestContext.currentSession ?: return

        // Add basic session info
        addHeader(headers, SESSION_ID, session.sessionId)
        addHeader(headers, USER_ID, session.userId)
        addHeader(headers, WEB_APP_ID, session.webApplicationId)

        // Add session-specific context
        when (session) {
            is TenantSession -> {
                addHeader(headers, SESSION_TYPE, "TENANT")
                addHeader(headers, TENANT_ID, session.tenantId)
                addHeader(headers, OPERATOR_ID, session.operatorId)
            }

            is BusinessUnitSession -> {
                addHeader(headers, SESSION_TYPE, "BUSINESS_UNIT")
                addHeader(headers, TENANT_ID, session.tenantId)
                addHeader(headers, BUSINESS_UNIT_ID, session.businessUnitId)
                addHeader(headers, OPERATOR_ID, session.operatorId)
            }

            else -> {
                addHeader(headers, SESSION_TYPE, "GUEST")
            }
        }

        log.debug(
            "Added context to gRPC call: sessionType={}, tenantId={}",
            when (session) {
                is TenantSession -> "TENANT"
                is BusinessUnitSession -> "BUSINESS_UNIT"
                else -> "GUEST"
            },
            when (session) {
                is TenantSession -> session.tenantId
                is BusinessUnitSession -> session.tenantId
                else -> null
            }
        )
    }

    private fun addHeader(headers: Metadata, key: String, value: String) {
        headers.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value)
    }
}
