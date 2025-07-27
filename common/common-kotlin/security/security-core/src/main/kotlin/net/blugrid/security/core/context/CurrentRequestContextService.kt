package net.blugrid.security.core.context

import jakarta.inject.Singleton
import net.blugrid.security.core.model.BaseAuthenticatedOrganisation
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.BaseAuthenticatedUser
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.TenantSession
import java.util.Optional

/**
 * Primary implementation - Use this when DI is available
 */
@Singleton
class CurrentRequestContextService(
    private val requestContextService: RequestContextService
) : RequestContextProvider {

    val currentAuthentication: DecoratedAuthentication<out BaseAuthenticatedSession>?
        get() = currentAuthenticationOpt.orElse(null)

    val currentAuthenticationOpt: Optional<DecoratedAuthentication<out BaseAuthenticatedSession>>
        get() = requestContextService.getCurrentAuthentication()

    override val currentSessionId: Long?
        get() = currentAuthentication?.session?.sessionId?.toLongOrNull()

    override val currentBusinessUnitId: Long?
        get() = when {
            BusinessUnitIdOverride.hasOverride() -> BusinessUnitIdOverride.value.toLong()
            else -> {
                val session = currentAuthentication?.session
                when (session) {
                    is BusinessUnitSession -> session.businessUnitId.toLongOrNull()
                    else -> null
                }
            }
        }

    override val currentTenantId: Long?
        get() = when {
            TenantIdOverride.hasOverride() -> TenantIdOverride.value.toLong()
            else -> {
                val session = currentAuthentication?.session
                when (session) {
                    is TenantSession -> session.tenantId.toLongOrNull()
                    is BusinessUnitSession -> session.tenantId.toLongOrNull()
                    else -> null
                }
            }
        }

    override val currentIsUnscoped: Boolean
        get() = IsUnscoped.isSet() && IsUnscoped.value

    override val currentOrganisation: BaseAuthenticatedOrganisation?
        get() = currentAuthentication?.let { auth ->
            // Get organisation from attributes if available
            auth.getAttributes()["organisation"] as? BaseAuthenticatedOrganisation
        }

    override val currentSession: BaseAuthenticatedSession?
        get() = currentAuthentication?.session

    override val currentUser: BaseAuthenticatedUser?
        get() = currentAuthentication?.user
}
