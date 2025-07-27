package net.blugrid.security.core.session

import net.blugrid.security.core.model.BaseAuthenticatedSession

data class BusinessUnitSession(
    override val sessionId: String,
    override val userId: String,
    override val webApplicationId: String,
    val tenantId: String,
    val businessUnitId: String,
    val operatorId: String
) : BaseAuthenticatedSession {
    override val sessionType: SessionType = SessionType.BUSINESS_UNIT
}
