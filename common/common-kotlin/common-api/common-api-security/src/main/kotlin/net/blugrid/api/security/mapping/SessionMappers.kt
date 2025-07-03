package net.blugrid.api.security.mapping

import net.blugrid.api.security.context.BusinessUnitSessionContext
import net.blugrid.api.security.context.GuestSessionContext
import net.blugrid.api.security.context.WebApplicationSessionContext
import net.blugrid.api.session.model.BusinessUnitSession
import net.blugrid.api.session.model.GuestSession
import net.blugrid.api.session.model.TenantSession

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
