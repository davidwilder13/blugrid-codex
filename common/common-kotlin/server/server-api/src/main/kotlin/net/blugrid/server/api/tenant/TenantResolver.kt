package net.blugrid.server.api.tenant

interface TenantResolver {
    fun resolveCurrentTenant(): TenantContext?
    fun validateTenant(context: TenantContext): Boolean = true
    val priority: Int get() = 0
}
