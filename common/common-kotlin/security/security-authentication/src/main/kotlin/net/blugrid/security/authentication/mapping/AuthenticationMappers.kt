package net.blugrid.security.authentication.mapping

import net.blugrid.security.authentication.model.BusinessUnitAuthentication
import net.blugrid.security.authentication.model.GuestAuthentication
import net.blugrid.security.authentication.model.TenantAuthentication
import net.blugrid.security.core.context.BusinessUnitSessionContext
import net.blugrid.security.core.context.GuestSessionContext
import net.blugrid.security.core.context.WebApplicationSessionContext

fun GuestSessionContext.toAuthentication(): GuestAuthentication =
    GuestAuthentication(
        providerId = user.providerId,
        principalName = user.name,
        principalEmail = user.email,
        sessionId = id.toString(),
        userId = user.id.toString(),
        user = user.toAuthenticatedUser(),
        session = this.toAuthenticatedSession()
    )

fun WebApplicationSessionContext.toAuthentication(): TenantAuthentication =
    TenantAuthentication(
        providerId = user.providerId,
        principalName = user.name,
        principalEmail = user.email,
        sessionId = id.toString(),
        userId = user.id.toString(),
        organisation = organisation.toAuthenticatedOrganisation(),
        session = this.toAuthenticatedSession(),
        user = user.toAuthenticatedUser()
    )

fun BusinessUnitSessionContext.toAuthentication(): BusinessUnitAuthentication =
    BusinessUnitAuthentication(
        providerId = user.providerId,
        principalName = user.name,
        principalEmail = user.email,
        sessionId = id.toString(),
        userId = user.id.toString(),
        organisation = organisation.toAuthenticatedOrganisation(),
        session = this.toAuthenticatedSession(),
        user = user.toAuthenticatedUser()
    )

fun TenantAuthentication.toBusinessUnitAuthentication(
    context: BusinessUnitSessionContext
): BusinessUnitAuthentication =
    BusinessUnitAuthentication(
        providerId = providerId,
        principalName = principalName,
        principalEmail = principalEmail,
        sessionId = sessionId,
        userId = userId,
        expirationTime = expirationTime,
        organisation = organisation,
        session = context.toAuthenticatedSession(),
        user = user
    )

