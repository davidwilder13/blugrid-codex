package net.blugrid.security.core.context

import net.blugrid.security.core.model.BaseAuthenticatedOrganisation
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.BaseAuthenticatedUser

interface RequestContextProvider {
    val currentSessionId: Long?
    val currentBusinessUnitId: Long?
    val currentTenantId: Long?
    val currentIsUnscoped: Boolean
    val currentOrganisation: BaseAuthenticatedOrganisation?
    val currentSession: BaseAuthenticatedSession?
    val currentUser: BaseAuthenticatedUser?
}

// Default implementation that delegates to AuthenticationResolver
class DefaultRequestContextProvider(
    private val authenticationResolver: AuthenticationResolver
) : RequestContextProvider {

    override val currentSessionId: Long?
        get() = authenticationResolver.currentSessionId

    override val currentBusinessUnitId: Long?
        get() = authenticationResolver.currentBusinessUnitId

    override val currentTenantId: Long?
        get() = authenticationResolver.currentTenantId

    override val currentIsUnscoped: Boolean
        get() = authenticationResolver.currentIsUnscoped

    override val currentOrganisation: BaseAuthenticatedOrganisation?
        get() = authenticationResolver.currentOrganisation

    override val currentSession: BaseAuthenticatedSession?
        get() = authenticationResolver.currentSession

    override val currentUser: BaseAuthenticatedUser?
        get() = authenticationResolver.currentUser
}
