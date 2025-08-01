package net.blugrid.server.api.config

import jakarta.inject.Singleton
import net.blugrid.security.core.context.RequestContext
import net.blugrid.server.api.tenant.TenantContext
import org.slf4j.LoggerFactory

/**
 * Central service for managing tenant context - integrated with existing security framework.
 *
 * This implementation bridges between:
 * - Your existing CurrentRequestContext (security/authentication system)
 * - The new server-api TenantContext abstraction
 * - Multi-tenant vs standalone deployment modes
 */
@Singleton
class TenantContextHolder {

    companion object {
        private val logger = LoggerFactory.getLogger(TenantContextHolder::class.java)

        // Default tenant ID for standalone deployments
        private const val STANDALONE_TENANT_ID = "default"
    }

    private val threadLocalOverride = ThreadLocal<TenantContext>()

    /**
     * Get current tenant context for this thread.
     *
     * Resolution order:
     * 1. Thread-local override (for testing/background jobs)
     * 2. CurrentRequestContext.currentTenantId (from authentication)
     * 3. Fallback to STANDALONE mode
     *
     * @return TenantContext - never null
     */
    fun getCurrentTenant(): TenantContext {
        // Check for thread-local override first (testing/background jobs)
        threadLocalOverride.get()?.let { return it }

        // Get tenant from current request context (authentication system)
        val tenantId = RequestContext.currentTenantId

        return when {
            tenantId != null -> {
                // Multi-tenant mode - tenant resolved from authentication
                val tenantIdString = tenantId.toString()
                val tenantName = getCurrentTenantName()

                logger.debug("Resolved tenant from request context: {}", tenantIdString)
                TenantContext.multiTenant(tenantIdString, tenantName)
            }

            else -> {
                // Standalone mode - no tenant in authentication (single-tenant deployment)
                logger.debug("No tenant in request context, using STANDALONE mode")
                TenantContext.STANDALONE
            }
        }
    }

    /**
     * Explicitly set tenant context for current thread.
     * Useful for:
     * - Testing scenarios
     * - Background jobs with known tenant
     * - Admin operations that need to impersonate tenants
     *
     * @param context The tenant context to set
     */
    fun setCurrentTenant(context: TenantContext) {
        logger.debug("Setting tenant context override: {}", context.tenantId)
        threadLocalOverride.set(context)
    }

    /**
     * Clear thread-local tenant override.
     * The system will fall back to using CurrentRequestContext.
     * Should be called at the end of background jobs to prevent memory leaks.
     */
    fun clearCurrentTenant() {
        logger.debug("Clearing tenant context override for thread: {}", Thread.currentThread().name)
        threadLocalOverride.remove()
    }

    /**
     * Check if a tenant context override is currently set for this thread
     */
    fun hasTenantOverride(): Boolean {
        return threadLocalOverride.get() != null
    }

    /**
     * Execute a block of code with a specific tenant context
     * Automatically restores the previous context when done
     */
    fun <T> withTenant(context: TenantContext, block: () -> T): T {
        val previousContext = threadLocalOverride.get()
        return try {
            setCurrentTenant(context)
            block()
        } finally {
            if (previousContext != null) {
                setCurrentTenant(previousContext)
            } else {
                clearCurrentTenant()
            }
        }
    }

    /**
     * Get the current business unit ID from the request context
     * This can be used for additional tenant scoping within a tenant
     */
    fun getCurrentBusinessUnitId(): Long? {
        return RequestContext.currentBusinessUnitId
    }

    /**
     * Check if the current request is unscoped (admin/system operations)
     */
    fun isCurrentRequestUnscoped(): Boolean {
        return RequestContext.currentIsUnscoped
    }

    /**
     * Get tenant name from the current organisation in the request context
     */
    private fun getCurrentTenantName(): String? {
        return RequestContext.currentOrganisation?.displayName
    }

    /**
     * Get debug information about current tenant resolution
     * Useful for troubleshooting and monitoring
     */
    fun getTenantDebugInfo(): TenantDebugInfo {
        val currentTenant = getCurrentTenant()
        val hasOverride = hasTenantOverride()
        val requestTenantId = RequestContext.currentTenantId
        val businessUnitId = getCurrentBusinessUnitId()
        val isUnscoped = isCurrentRequestUnscoped()

        return TenantDebugInfo(
            resolvedTenant = currentTenant,
            hasThreadLocalOverride = hasOverride,
            requestContextTenantId = requestTenantId,
            requestContextBusinessUnitId = businessUnitId,
            isRequestUnscoped = isUnscoped,
            resolutionSource = when {
                hasOverride -> "Thread Local Override"
                requestTenantId != null -> "Request Context (Authentication)"
                else -> "Standalone Fallback"
            }
        )
    }
}

/**
 * Debug information about tenant resolution
 */
data class TenantDebugInfo(
    val resolvedTenant: TenantContext,
    val hasThreadLocalOverride: Boolean,
    val requestContextTenantId: Long?,
    val requestContextBusinessUnitId: Long?,
    val isRequestUnscoped: Boolean,
    val resolutionSource: String
)
