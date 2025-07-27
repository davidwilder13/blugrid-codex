package net.blugrid.server.persistence.scope

/**
 * Service abstraction for database scope management via PostgreSQL functions
 * Hides JPA repository details from business services
 */
interface RequestScopeService {
    /**
     * Set tenant scope using PostgreSQL function: set_tenant_scope(tenant_id)
     */
    fun setTenantScope(tenantId: String): Int

    /**
     * Set business unit scope using PostgreSQL function: set_business_unit_scope(business_unit_id)
     */
    fun setBusinessUnitScope(businessUnitId: String): Int

    /**
     * Reset/clear request scope using PostgreSQL function: reset_request_scope()
     */
    fun resetRequestScope(): Int

    // Convenience methods for common operations
    fun setTenantScope(tenantId: Long): Int = setTenantScope(tenantId.toString())
    fun setBusinessUnitScope(businessUnitId: Long): Int = setBusinessUnitScope(businessUnitId.toString())
}
