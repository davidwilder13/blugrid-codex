package net.blugrid.api.security.context

import net.blugrid.api.security.model.BaseAuthenticatedOrganisation
import net.blugrid.api.security.model.BaseAuthenticatedSession
import net.blugrid.api.security.model.BaseAuthenticatedUser

interface RequestContextProvider {
    val currentTenantId: Long?
    val currentBusinessUnitId: Long?
    val currentSessionId: Long?
    val currentSession: BaseAuthenticatedSession?
    val currentUser: BaseAuthenticatedUser?
    val currentOrganisation: BaseAuthenticatedOrganisation?

    val currentIsUnscoped: Boolean
}
