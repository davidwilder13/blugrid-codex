package net.blugrid.api.session.model

import net.blugrid.api.security.model.BaseAuthenticatedSession

data class BusinessUnitSession(
    override val sessionId: String,
    override val userId: String,
    override val webApplicationId: String,
    val tenantId: String,
    val operatorId: String,
    val businessUnitId: String
) : BaseAuthenticatedSession {
    override val sessionType: SessionType = SessionType.BUSINESS_UNIT
}
