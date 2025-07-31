package net.blugrid.integration.grpc.interceptor

import io.grpc.ForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.micronaut.core.order.Ordered
import io.micronaut.core.propagation.PropagatedContext
import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.security.authentication.model.BusinessUnitAuthentication
import net.blugrid.security.authentication.model.GuestAuthentication
import net.blugrid.security.authentication.model.TenantAuthentication
import net.blugrid.security.core.context.AuthenticationContextElement
import net.blugrid.security.core.context.TenantContextPropagationElement
import net.blugrid.security.core.model.AuthenticatedOrganisation
import net.blugrid.security.core.model.AuthenticatedUser
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.GuestSession
import net.blugrid.security.core.session.TenantSession

/**
 * AuthServerInterceptor - Fixed Version
 *
 * Key Fix: Store authentication in BOTH propagation mechanisms:
 * 1. AuthenticationContextElement (for CurrentRequestContext.tryGetPropagatedAuthentication)
 * 2. TenantContextPropagationElement (for ThreadLocal access by sequence generator)
 *
 * This ensures authentication works for both business logic and Hibernate operations.
 */
@Singleton
class AuthServerInterceptor : ServerInterceptor, Ordered {

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

    private val log = logger()

    /**
     * Set high priority (low number) to ensure auth context is set early
     * in the interceptor chain, before other interceptors that might need it
     */
    override fun getOrder(): Int = 100

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {

        // Extract authentication from gRPC headers
        val authentication = reconstructAuthenticationFromHeaders(headers)

        if (authentication != null) {
            val tenantId = getTenantIdFromAuth(authentication)
            log.debug(
                "🚀 Setting gRPC context with DUAL propagation: sessionType={}, tenantId={}, thread={}",
                authentication.authenticationType,
                tenantId,
                Thread.currentThread().name
            )

            // CRITICAL FIX: Store authentication in BOTH propagation mechanisms
            val authContextElement = AuthenticationContextElement(authentication)
            val tenantContextElement = TenantContextPropagationElement(authentication)

            val newContext = PropagatedContext.getOrEmpty()
                .plus(authContextElement)      // For CurrentRequestContext.tryGetPropagatedAuthentication
                .plus(tenantContextElement)    // For ThreadLocal access

            // Propagate for entire call - both mechanisms active
            val scope = newContext.propagate()
            log.debug("📌 Context propagated with DUAL support (AuthElement + ThreadLocal)...")
            val listener = next.startCall(call, headers)
            return ContextAwareListener(listener, scope, authentication)
        } else {
            log.debug("No authentication found in gRPC headers - proceeding without context")
            return next.startCall(call, headers)
        }
    }

    private fun getTenantIdFromAuth(authentication: DecoratedAuthentication<out BaseAuthenticatedSession>): String? {
        return when (authentication.session) {
            is TenantSession -> (authentication.session as TenantSession).tenantId
            is BusinessUnitSession -> (authentication.session as BusinessUnitSession).tenantId
            else -> null
        }
    }

    /**
     * Reconstruct authentication from headers - simple and fast
     * Returns null if insufficient context (let service layer handle it)
     */
    private fun reconstructAuthenticationFromHeaders(headers: Metadata): DecoratedAuthentication<out BaseAuthenticatedSession>? {
        val sessionId = getHeader(headers, SESSION_ID) ?: return null
        val sessionType = getHeader(headers, SESSION_TYPE) ?: return null
        val userId = getHeader(headers, USER_ID) ?: return null
        val webAppId = getHeader(headers, WEB_APP_ID) ?: return null

        return when (sessionType) {
            "GUEST" -> createGuestAuthentication(sessionId, userId, webAppId)

            "TENANT" -> {
                val tenantId = getHeader(headers, TENANT_ID) ?: return null
                val operatorId = getHeader(headers, OPERATOR_ID) ?: return null
                createTenantAuthentication(sessionId, userId, webAppId, tenantId, operatorId)
            }

            "BUSINESS_UNIT" -> {
                val tenantId = getHeader(headers, TENANT_ID) ?: return null
                val businessUnitId = getHeader(headers, BUSINESS_UNIT_ID) ?: return null
                val operatorId = getHeader(headers, OPERATOR_ID) ?: return null
                createBusinessUnitAuthentication(sessionId, userId, webAppId, tenantId, businessUnitId, operatorId)
            }

            else -> null
        }
    }

    private fun getHeader(headers: Metadata, key: String): String? {
        return headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))
    }

    // Simple authentication object creation - minimal data for context
    private fun createGuestAuthentication(sessionId: String, userId: String, webAppId: String): GuestAuthentication {
        return GuestAuthentication(
            providerId = "grpc-internal",
            principalName = "gRPC User",
            principalEmail = "grpc@internal.api",
            sessionId = sessionId,
            userId = userId,
            user = createMinimalUser(userId),
            session = GuestSession(sessionId, userId, webAppId)
        )
    }

    private fun createTenantAuthentication(
        sessionId: String,
        userId: String,
        webAppId: String,
        tenantId: String,
        operatorId: String
    ): TenantAuthentication {
        return TenantAuthentication(
            providerId = "grpc-internal",
            principalName = "gRPC User",
            principalEmail = "grpc@internal.api",
            sessionId = sessionId,
            userId = userId,
            organisation = createMinimalOrganisation(tenantId),
            session = TenantSession(sessionId, userId, webAppId, tenantId, operatorId),
            user = createMinimalUser(userId)
        )
    }

    private fun createBusinessUnitAuthentication(
        sessionId: String,
        userId: String,
        webAppId: String,
        tenantId: String,
        businessUnitId: String,
        operatorId: String
    ): BusinessUnitAuthentication {
        return BusinessUnitAuthentication(
            providerId = "grpc-internal",
            principalName = "gRPC User",
            principalEmail = "grpc@internal.api",
            sessionId = sessionId,
            userId = userId,
            organisation = createMinimalOrganisation(tenantId),
            session = BusinessUnitSession(sessionId, userId, webAppId, tenantId, businessUnitId, operatorId),
            user = createMinimalUser(userId)
        )
    }

    // Minimal user/org objects - just enough for context
    private fun createMinimalUser(userId: String): AuthenticatedUser {
        return AuthenticatedUser(
            userIdentityId = userId,
            email = "grpc@internal.api",
            providerId = "grpc-internal",
            displayName = "gRPC User"
        )
    }

    private fun createMinimalOrganisation(tenantId: String): AuthenticatedOrganisation {
        return AuthenticatedOrganisation(
            tenantId = tenantId,
            displayName = "gRPC Org"
        )
    }
}

/**
 * Context-aware listener for cleanup
 *
 */
private class ContextAwareListener<ReqT>(
    private val delegate: ServerCall.Listener<ReqT>,
    private val contextScope: PropagatedContext.Scope,
    private val authentication: DecoratedAuthentication<out BaseAuthenticatedSession>
) : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {

    private val log = logger()

    init {
        val tenantId = when (authentication.session) {
            is TenantSession -> (authentication.session as TenantSession).tenantId
            is BusinessUnitSession -> (authentication.session as BusinessUnitSession).tenantId
            else -> null
        }
        log.debug("🎯 Context listener created - will maintain scope for tenantId={}", tenantId)
    }

    override fun onComplete() {
        try {
            super.onComplete()
        } finally {
            log.debug("🧹 Cleaning up gRPC context on completion")
            contextScope.close()
        }
    }

    override fun onCancel() {
        try {
            super.onCancel()
        } finally {
            log.debug("🧹 Cleaning up gRPC context (cancelled)")
            contextScope.close()
        }
    }
}
