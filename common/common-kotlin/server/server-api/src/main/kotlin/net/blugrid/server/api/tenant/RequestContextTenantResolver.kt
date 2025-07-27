package net.blugrid.server.api.tenant

import jakarta.inject.Singleton
import net.blugrid.security.core.context.CurrentRequestContext

/**
 * Primary tenant resolver that uses your existing CurrentRequestContext system.
 * This eliminates the need for multiple resolver implementations.
 */
@Singleton
class RequestContextTenantResolver : TenantResolver {

    override fun resolveCurrentTenant(): TenantContext? {
        val tenantId = CurrentRequestContext.currentTenantId

        return when {
            tenantId != null -> {
                // Multi-tenant mode - tenant found in authentication context
                val tenantIdString = tenantId.toString()
                val tenantName = getCurrentTenantName()

                TenantContext.multiTenant(tenantIdString, tenantName)
            }

            else -> {
                // Could be standalone mode, or no authentication present
                // Return null to let TenantContextHolder decide fallback
                null
            }
        }
    }

    override fun validateTenant(context: TenantContext): Boolean {
        return when {
            context.isStandalone -> true // Standalone is always valid
            context.tenantId.isNotBlank() -> {
                // For multi-tenant, verify it matches current request context
                val currentTenantId = CurrentRequestContext.currentTenantId
                currentTenantId?.toString() == context.tenantId
            }

            else -> false
        }
    }

    override val priority: Int = 100 // Highest priority - this is the primary resolver

    /**
     * Extract tenant name from current organisation
     */
    private fun getCurrentTenantName(): String? {
        return CurrentRequestContext.currentOrganisation?.let { org ->
            // You'll need to adjust this based on your BaseAuthenticatedOrganisation structure
            extractOrganisationName(org)
        }
    }

    /**
     * Helper to extract name from organisation object
     * Adjust this method based on your actual BaseAuthenticatedOrganisation implementation
     */
    private fun extractOrganisationName(org: Any): String? {
        return try {
            // Use reflection to get name field - adjust field names as needed
            val nameField = org.javaClass.declaredFields.find {
                it.name in listOf("name", "organisationName", "orgName", "displayName")
            }
            nameField?.let {
                it.isAccessible = true
                it.get(org) as? String
            }
        } catch (e: Exception) {
            null
        }
    }
}
