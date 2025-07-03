package net.blugrid.api.security.mapping

import net.blugrid.api.security.context.BusinessUnitSessionContext
import net.blugrid.api.security.context.GuestSessionContext
import net.blugrid.api.security.context.WebApplicationSessionContext
import net.blugrid.api.security.model.BusinessUnitAuthentication
import net.blugrid.api.security.model.GuestAuthentication
import net.blugrid.api.security.model.TenantAuthentication

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

