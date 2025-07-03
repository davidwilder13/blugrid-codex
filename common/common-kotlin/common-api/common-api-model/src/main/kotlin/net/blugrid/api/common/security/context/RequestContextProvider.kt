package net.blugrid.api.common.security.context

interface RequestContextProvider {
    val currentTenantId: Long?
    val currentBusinessUnitId: Long?
    val currentSessionId: Long?
    val currentIsUnscoped: Boolean
}
