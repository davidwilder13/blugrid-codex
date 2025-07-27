package net.blugrid.platform.testing.support

import net.blugrid.security.core.context.BusinessUnitIdOverride
import net.blugrid.security.core.context.IsUnscoped
import net.blugrid.security.core.context.TenantIdOverride
import net.blugrid.security.core.context.doInRequestContext

/**
 * Helper functions for test context setup that replicate real-world scenarios
 */

/**
 * Execute action within a tenant-scoped request context
 */
fun <T> doInTenantContext(tenantId: Long, action: () -> T): T {
    return doInRequestContext {
        TenantIdOverride.value = tenantId.toString()
        action()
    }
}

/**
 * Execute action within a business unit-scoped request context
 */
fun <T> doInBusinessUnitContext(tenantId: Long, businessUnitId: Long, action: () -> T): T {
    return doInRequestContext {
        TenantIdOverride.value = tenantId.toString()
        BusinessUnitIdOverride.value = businessUnitId.toString()
        action()
    }
}

/**
 * Execute action within an unscoped request context
 */
fun <T> doInUnscopedContext(action: () -> T): T {
    return doInRequestContext {
        IsUnscoped.value = true
        action()
    }
}

/**
 * Execute action in a clean request context (no overrides)
 */
fun <T> doInCleanContext(action: () -> T): T {
    return doInRequestContext {
        // Don't set any overrides - use whatever comes from authentication
        action()
    }
}
