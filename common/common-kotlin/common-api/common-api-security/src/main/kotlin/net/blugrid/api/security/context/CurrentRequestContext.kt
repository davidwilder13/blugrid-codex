package net.blugrid.api.security.context

import io.micronaut.http.HttpRequest
import io.micronaut.http.context.ServerRequestContext
import net.blugrid.api.common.security.context.RequestContextProvider
import net.blugrid.api.security.authentication.mapping.toMultitenantAuthentication
import net.blugrid.api.security.authentication.model.AuthenticatedBusinessUnitSession
import net.blugrid.api.security.authentication.model.AuthenticatedOrganisation
import net.blugrid.api.security.authentication.model.AuthenticatedSession
import net.blugrid.api.security.authentication.model.AuthenticatedUser
import net.blugrid.api.security.authentication.model.AuthenticatedWebApplicationSession
import net.blugrid.api.security.authentication.model.BusinessUnitAuthentication
import net.blugrid.api.security.authentication.model.DecoratedAuthentication
import net.blugrid.api.security.authentication.model.GuestAuthentication
import net.blugrid.api.security.authentication.model.TenantAuthentication
import java.util.Optional

object CurrentRequestContext : RequestContextProvider {

    override val currentSessionId: Long?
        get() = currentSession?.sessionId?.toLong()

    override val currentBusinessUnitId: Long?
        get() = if (BusinessUnitIdOverride.hasOverride()) {
            BusinessUnitIdOverride.value.toLong()
        } else {
            when (currentSession) {
                is AuthenticatedWebApplicationSession -> null
                is AuthenticatedBusinessUnitSession -> (currentSession as AuthenticatedBusinessUnitSession)
                    .businessUnitId
                    .toLong()

                else -> null
            }
        }

    override val currentTenantId: Long?
        get() = if (TenantIdOverride.hasOverride()) {
            TenantIdOverride.value.toLong()
        } else {
            when (currentSession) {
                is AuthenticatedWebApplicationSession -> (currentSession as AuthenticatedWebApplicationSession)
                    .tenantId
                    .toLong()

                is AuthenticatedBusinessUnitSession -> (currentSession as AuthenticatedBusinessUnitSession)
                    .tenantId.toLong()

                else -> null
            }
        }

    override val currentIsUnscoped: Boolean
        get() = if (IsUnscoped.isSet()) {
            IsUnscoped.value
        } else {
            false
        }

    val currentOrganisation: AuthenticatedOrganisation?
        get() = when (authentication) {
            is TenantAuthentication -> (authentication as TenantAuthentication).organisation
            is BusinessUnitAuthentication -> (authentication as BusinessUnitAuthentication).organisation
            else -> null
        }


    val currentSession: AuthenticatedSession?
        get() = when (authentication) {
            is GuestAuthentication -> (authentication as GuestAuthentication).session
            is TenantAuthentication -> (authentication as TenantAuthentication).session
            is BusinessUnitAuthentication -> (authentication as BusinessUnitAuthentication).session
            else -> null
        }

    val currentUser: AuthenticatedUser?
        get() = when (authentication) {
            is GuestAuthentication -> (authentication as GuestAuthentication).user
            is TenantAuthentication -> (authentication as TenantAuthentication).user
            is BusinessUnitAuthentication -> (authentication as BusinessUnitAuthentication).user
            else -> null
        }

    val authentication: DecoratedAuthentication?
        get() = authenticationOpt.orElse(null)


    val authenticationOpt: Optional<DecoratedAuthentication>
        get() = ServerRequestContext.currentRequest<Any>()
            .flatMap { request: HttpRequest<Any> ->
                request.toMultitenantAuthentication()
            }
}
