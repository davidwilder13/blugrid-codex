package net.blugrid.security.core.context

import io.micronaut.core.propagation.PropagatedContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.context.ServerRequestContext
import io.micronaut.security.filters.SecurityFilter
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.model.BaseAuthenticatedOrganisation
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.BaseAuthenticatedUser
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.TenantSession
import java.util.Optional
import kotlin.jvm.java

/**
 * Current Request Context - Clean Authentication Access Layer
 *
 * Mental Model: High-level authentication API that works universally
 * Uses RequestContextAdapter under the hood to abstract HTTP vs gRPC differences
 *
 * This class provides business-level authentication operations while hiding
 * the complexity of protocol-specific context management.
 *
 * Clean Architecture:
 * - No direct dependencies on gRPC or HTTP modules
 * - Uses only shared CommonRequestContextHolder
 * - Protocol-agnostic business logic
 * - Maintains backward compatibility with existing RequestContextProvider contract
 */
object CurrentRequestContext : RequestContextProvider {

    private val log = logger()

    // ===== CORE AUTHENTICATION PROPERTIES =====

    /**
     * Get current authentication - works for both HTTP and gRPC
     *
     * Mental Model: Universal authentication resolver
     * HTTP → ServerRequestContext → Authentication
     * gRPC → CommonRequestContextHolder → HTTP Request → Authentication
     */
    val currentAuthenticationOpt: Optional<DecoratedAuthentication<out BaseAuthenticatedSession>>
        get() {
            log.trace("Resolving current authentication...")

            // Strategy 1: HTTP Context (ServerRequestContext - automatic from Micronaut)
            val httpAuth = tryGetHttpAuthentication()
            if (httpAuth != null) {
                log.trace("✅ Found HTTP authentication: {} ({})", httpAuth.principalName, httpAuth.authenticationType)
                return Optional.of(httpAuth)
            }

            // Strategy 2: PropagatedContext (gRPC)
            val propagatedAuth = tryGetPropagatedAuthentication()
            if (propagatedAuth != null) {
                log.trace("✅ PropagatedContext authentication: {}", propagatedAuth.principalName)
                return Optional.of(propagatedAuth)
            }

            log.trace("❌ No authentication found in any context")
            return Optional.empty()
        }

    // ===== AUTHENTICATION RESOLUTION STRATEGIES =====

    /**
     * Strategy 1: HTTP authentication via Micronaut's ServerRequestContext
     * Works automatically for HTTP requests
     */
    private fun tryGetHttpAuthentication(): DecoratedAuthentication<out BaseAuthenticatedSession>? {
        return try {
            val httpRequestOpt = ServerRequestContext.currentRequest<Any>()
            if (httpRequestOpt.isPresent) {
                val request = httpRequestOpt.get()
                log.trace("Found HTTP request context, extracting authentication...")
                request.toMultitenantAuthentication().orElse(null)
            } else {
                log.trace("No HTTP request context available")
                null
            }
        } catch (e: Exception) {
            log.trace("HTTP authentication extraction failed: ${e.message}")
            null
        }
    }

    /**
     * Strategy 2: gRPC authentication via PropagatedContext
     * Set by gRPC interceptors using AuthenticationContextElement
     */
    private fun tryGetPropagatedAuthentication(): DecoratedAuthentication<out BaseAuthenticatedSession>? {
        return try {
            if (PropagatedContext.exists()) {
                val context = PropagatedContext.get()
                val authElementOpt = context.find(AuthenticationContextElement::class.java)

                if (authElementOpt.isPresent) {
                    log.trace("Found authentication in PropagatedContext")
                    authElementOpt.get().authentication
                } else {
                    log.trace("PropagatedContext exists but no authentication element found")
                    null
                }
            } else {
                log.trace("No PropagatedContext available")
                null
            }
        } catch (e: Exception) {
            log.trace("PropagatedContext authentication extraction failed: ${e.message}")
            null
        }
    }

    /**
     * Get current authentication (non-optional) - throws if not found
     * Maintains compatibility with old contract
     */
    val currentAuthentication: DecoratedAuthentication<out BaseAuthenticatedSession>?
        get() = currentAuthenticationOpt.orElse(null)

    // ===== RequestContextProvider CONTRACT IMPLEMENTATION =====

    /**
     * Get current session ID as Long (maintains old contract)
     */
    override val currentSessionId: Long?
        get() = currentAuthentication?.session?.sessionId?.toLongOrNull()

    /**
     * Get current business unit ID as Long with override support (maintains old contract)
     */
    override val currentBusinessUnitId: Long?
        get() = when {
            BusinessUnitIdOverride.hasOverride() -> BusinessUnitIdOverride.value.toLong()
            else -> {
                val session = currentSession
                when (session) {
                    is BusinessUnitSession -> session.businessUnitId.toLongOrNull()
                    else -> null
                }
            }
        }

    /**
     * Get current tenant ID as Long with override support (maintains old contract)
     */
    override val currentTenantId: Long?
        get() = when {
            TenantIdOverride.hasOverride() -> TenantIdOverride.value.toLong()
            else -> {
                val session = currentSession
                when (session) {
                    is TenantSession -> session.tenantId.toLongOrNull()
                    is BusinessUnitSession -> session.tenantId.toLongOrNull()
                    else -> null
                }
            }
        }

    /**
     * Check if current context is unscoped (maintains old contract)
     */
    override val currentIsUnscoped: Boolean
        get() = IsUnscoped.isSet() && IsUnscoped.value

    /**
     * Get current organisation (maintains old contract)
     */
    override val currentOrganisation: BaseAuthenticatedOrganisation?
        get() = currentAuthentication?.let { auth ->
            auth.getAttributes()["organisation"] as? BaseAuthenticatedOrganisation
        }

    /**
     * Get current session (maintains old contract)
     */
    override val currentSession: BaseAuthenticatedSession?
        get() {
            log.trace("Resolving current session...")
            return currentAuthenticationOpt
                .map { auth ->
                    log.trace(
                        "✅ Session found: {} for user {}",
                        auth.session::class.simpleName, auth.principalName
                    )
                    auth.session
                }
                .orElse(null)
        }

    /**
     * Get current user (maintains old contract)
     */
    override val currentUser: BaseAuthenticatedUser?
        get() = currentAuthentication?.user
}

// ===== EXTENSION FUNCTION (maintains old contract) =====

@Suppress("UNCHECKED_CAST")
fun HttpRequest<*>.toMultitenantAuthentication(): Optional<DecoratedAuthentication<out BaseAuthenticatedSession>> {
    val attributeValue = attributes.getValue(SecurityFilter.AUTHENTICATION)
    return if (attributeValue is DecoratedAuthentication<*>) {
        Optional.of(attributeValue)
    } else {
        Optional.empty()
    }
}
