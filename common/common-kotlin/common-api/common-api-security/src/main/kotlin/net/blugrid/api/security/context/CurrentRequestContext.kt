package net.blugrid.api.security.context

import io.micronaut.http.context.ServerRequestContext
import net.blugrid.api.security.model.BaseAuthenticatedOrganisation
import net.blugrid.api.security.model.BaseAuthenticatedSession
import net.blugrid.api.security.model.BaseAuthenticatedUser
import net.blugrid.api.security.model.BusinessUnitAuthentication
import net.blugrid.api.security.model.DecoratedAuthentication
import net.blugrid.api.security.model.GuestAuthentication
import net.blugrid.api.security.model.TenantAuthentication
import toMultitenantAuthentication
import java.util.Optional

object CurrentRequestContext : RequestContextProvider {

    override val currentSessionId: Long?
        get() = authentication?.session?.sessionId?.toLongOrNull()

    override val currentBusinessUnitId: Long?
        get() = when {
            BusinessUnitIdOverride.hasOverride() -> BusinessUnitIdOverride.value.toLong()
            authentication is BusinessUnitAuthentication -> (authentication as BusinessUnitAuthentication)
                .session
                .businessUnitId
                .toLongOrNull()

            else -> null
        }

    override val currentTenantId: Long?
        get() = when {
            TenantIdOverride.hasOverride() -> TenantIdOverride.value.toLong()
            authentication is TenantAuthentication -> (authentication as TenantAuthentication)
                .session
                .tenantId
                .toLongOrNull()

            authentication is BusinessUnitAuthentication -> (authentication as BusinessUnitAuthentication)
                .session
                .tenantId
                .toLongOrNull()

            else -> null
        }

    override val currentIsUnscoped: Boolean
        get() = IsUnscoped.isSet() && IsUnscoped.value

    override val currentOrganisation: BaseAuthenticatedOrganisation?
        get() = when (authentication) {
            is TenantAuthentication -> (authentication as TenantAuthentication).organisation
            is BusinessUnitAuthentication -> (authentication as BusinessUnitAuthentication).organisation
            else -> null
        }

    override val currentSession: BaseAuthenticatedSession?
        get() = when (authentication) {
            is GuestAuthentication -> (authentication as GuestAuthentication).session
            is TenantAuthentication -> (authentication as TenantAuthentication).session
            is BusinessUnitAuthentication -> (authentication as BusinessUnitAuthentication).session
            else -> null
        }

    override val currentUser: BaseAuthenticatedUser?
        get() = when (authentication) {
            is GuestAuthentication -> (authentication as GuestAuthentication).user
            is TenantAuthentication -> (authentication as TenantAuthentication).user
            is BusinessUnitAuthentication -> (authentication as BusinessUnitAuthentication).user
            else -> null
        }

    val authentication: DecoratedAuthentication<out BaseAuthenticatedSession>?
        get() = authenticationOpt.orElse(null)

    val authenticationOpt: Optional<DecoratedAuthentication<out BaseAuthenticatedSession>>
        get() = ServerRequestContext.currentRequest<Any>()
            .flatMap { it.toMultitenantAuthentication() }
}
