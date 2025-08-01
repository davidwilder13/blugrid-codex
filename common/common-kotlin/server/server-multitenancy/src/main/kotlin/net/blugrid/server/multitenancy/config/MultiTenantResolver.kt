package net.blugrid.server.multitenancy.config

import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import net.blugrid.platform.logging.logger
import net.blugrid.security.core.context.RequestContext
import org.hibernate.context.spi.CurrentTenantIdentifierResolver

/**
 * Hibernate tenant resolver that integrates with your existing CurrentRequestContext.
 * This replaces the placeholder implementation with real tenant resolution.
 */
@Singleton
@Named("currentTenantResolver")
class MultiTenantResolver @Inject constructor(
    private val currentRequestContext: RequestContext
) : CurrentTenantIdentifierResolver<String> {

    private val log = logger()

    companion object {
        private const val DEFAULT_TENANT_ID = "default"
        private const val STANDALONE_TENANT_ID = "standalone"
    }

    override fun resolveCurrentTenantIdentifier(): String {
        return try {
            when {
                // Check if request is unscoped (admin operations)
                currentRequestContext.currentIsUnscoped -> {
                    log.debug("Unscoped request detected, using default tenant")
                    DEFAULT_TENANT_ID
                }

                // Get tenant from security context (your existing authentication system)
                currentRequestContext.currentTenantId != null -> {
                    val tenantId = currentRequestContext.currentTenantId.toString()
                    log.debug("Resolved tenant from SecurityContext: {}", tenantId)
                    tenantId
                }

                // No tenant found - could be standalone mode or unauthenticated request
                else -> {
                    log.debug("No tenant found in SecurityContext, using default")
                    DEFAULT_TENANT_ID
                }
            }
        } catch (e: Exception) {
            // If anything goes wrong, fall back to default
            log.warn("Error resolving tenant identifier: {}", e.message)
            DEFAULT_TENANT_ID
        }
    }

    override fun validateExistingCurrentSessions(): Boolean {
        return true // Allow existing sessions to continue
    }

    /**
     * Get the current tenant ID that would be resolved
     * Useful for debugging and testing
     */
    fun getCurrentTenantId(): String {
        return resolveCurrentTenantIdentifier()
    }

    /**
     * Check if the current request has a valid tenant context
     */
    fun hasValidTenantContext(): Boolean {
        return try {
            val tenantId = currentRequestContext.currentTenantId
            tenantId != null || currentRequestContext.currentIsUnscoped
        } catch (e: Exception) {
            false
        }
    }
}
