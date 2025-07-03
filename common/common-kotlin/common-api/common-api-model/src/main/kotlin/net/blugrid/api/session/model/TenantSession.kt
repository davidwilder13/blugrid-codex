package net.blugrid.api.session.model

import net.blugrid.api.security.model.BaseAuthenticatedSession

data class TenantSession(
    override val sessionId: String,
    override val userId: String,
    override val webApplicationId: String,
    val tenantId: String,
    val operatorId: String
) : BaseAuthenticatedSession {
    override val sessionType: SessionType = SessionType.WEB_APPLICATION
}
