package net.blugrid.security.core.context

import net.blugrid.security.core.model.BaseAuthenticatedOrganisation
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.BaseAuthenticatedUser
import net.blugrid.security.core.model.DecoratedAuthentication
import java.util.Optional

interface AuthenticationResolver {
    val currentAuthentication: DecoratedAuthentication<out BaseAuthenticatedSession>?
    val currentAuthenticationOpt: Optional<DecoratedAuthentication<out BaseAuthenticatedSession>>

    val currentSessionId: Long?
    val currentBusinessUnitId: Long?
    val currentTenantId: Long?
    val currentIsUnscoped: Boolean
    val currentOrganisation: BaseAuthenticatedOrganisation?
    val currentSession: BaseAuthenticatedSession?
    val currentUser: BaseAuthenticatedUser?
}
