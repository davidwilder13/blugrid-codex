package net.blugrid.security.authentication.mapping

import net.blugrid.security.core.context.BusinessUnitSessionContext
import net.blugrid.security.core.context.GuestSessionContext
import net.blugrid.security.core.context.WebApplicationSessionContext
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.GuestSession
import net.blugrid.security.core.session.TenantSession

fun GuestSessionContext.toAuthenticatedSession() = GuestSession(
    sessionId = id.toString(),
    userId = user.id.toString(),
    webApplicationId = webApplicationId.toString(),
)

fun WebApplicationSessionContext.toAuthenticatedSession() = TenantSession(
    sessionId = id.toString(),
    userId = user.id.toString(),
    tenantId = organisation.id.toString(),
    webApplicationId = webApplicationId.toString(),
    operatorId = operatorId.toString()
)

fun BusinessUnitSessionContext.toAuthenticatedSession() = BusinessUnitSession(
    sessionId = id.toString(),
    userId = user.id.toString(),
    tenantId = organisation.id.toString(),
    webApplicationId = webApplicationId.toString(),
    operatorId = operatorId.toString(),
    businessUnitId = businessUnitId.toString()
)
