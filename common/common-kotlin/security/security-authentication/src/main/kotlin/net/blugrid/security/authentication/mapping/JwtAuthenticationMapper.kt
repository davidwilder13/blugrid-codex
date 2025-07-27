package net.blugrid.security.authentication.mapping

import io.micronaut.security.authentication.Authentication
import net.blugrid.security.authentication.model.BusinessUnitAuthentication
import net.blugrid.security.authentication.model.GuestAuthentication
import net.blugrid.security.authentication.model.TenantAuthentication
import net.blugrid.security.core.model.AuthenticatedOrganisation
import net.blugrid.security.core.model.AuthenticationType
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.GuestSession
import net.blugrid.security.core.session.TenantSession
import net.blugrid.security.tokens.model.JwtToken

/**
 * Maps a JwtToken + AuthenticationType into a concrete Authentication implementation
 * (Tenant, Guest, or BusinessUnit).
 */
fun JwtToken.toAuthentication(type: AuthenticationType): Authentication = when (type) {
    AuthenticationType.GUEST -> toGuestAuthentication()
    AuthenticationType.TENANT -> toTenantAuthentication()
    AuthenticationType.BUSINESS_UNIT -> toBusinessUnitAuthentication()
}

fun JwtToken.toGuestAuthentication(): GuestAuthentication = GuestAuthentication(
    providerId = user.providerId,
    principalName = user.displayName ?: user.email,
    principalEmail = user.email,
    sessionId = session.sessionId,
    userId = user.userIdentityId,
    expirationTime = expirationTime,
    session = session as GuestSession,
    user = user
)

fun JwtToken.toTenantAuthentication(): TenantAuthentication = TenantAuthentication(
    providerId = user.providerId,
    principalName = user.displayName ?: user.email,
    principalEmail = user.email,
    sessionId = session.sessionId,
    userId = user.userIdentityId,
    organisation = organisation as AuthenticatedOrganisation,
    session = session as TenantSession,
    user = user
)

fun JwtToken.toBusinessUnitAuthentication(): BusinessUnitAuthentication = BusinessUnitAuthentication(
    providerId = user.providerId,
    principalName = user.displayName ?: user.email,
    principalEmail = user.email,
    sessionId = session.sessionId,
    userId = user.userIdentityId,
    organisation = organisation as AuthenticatedOrganisation,
    session = session as BusinessUnitSession,
    user = user
)

fun DecoratedAuthentication<*>.toJwtToken(): JwtToken = when (this) {
    is GuestAuthentication -> toGuestJwtToken()
    is TenantAuthentication -> toTenantJwtToken()
    is BusinessUnitAuthentication -> toBusinessUnitJwtToken()
    else -> throw IllegalArgumentException("Unsupported authentication: $this")
}

fun GuestAuthentication.toGuestJwtToken(): JwtToken =
    JwtToken(
        authenticationType = authenticationType,
        user = user,
        session = session,
        expirationTime = expirationTime,
    )

fun TenantAuthentication.toTenantJwtToken(): JwtToken =
    JwtToken(
        authenticationType = authenticationType,
        user = user,
        session = session,
        organisation = organisation,
        expirationTime = expirationTime,
    )

fun BusinessUnitAuthentication.toBusinessUnitJwtToken(): JwtToken =
    JwtToken(
        authenticationType = authenticationType,
        user = user,
        session = session,
        organisation = organisation,
        expirationTime = expirationTime,
    )
