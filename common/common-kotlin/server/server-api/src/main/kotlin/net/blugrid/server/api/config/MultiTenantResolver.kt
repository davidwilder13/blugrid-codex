package net.blugrid.server.api.config

interface MultiTenantResolver {
    fun resolveCurrentTenantIdentifier(): String?
    fun validateExistingCurrentSessions(): Boolean
}
