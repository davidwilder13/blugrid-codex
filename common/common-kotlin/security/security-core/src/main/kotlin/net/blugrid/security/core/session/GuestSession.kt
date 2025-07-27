package net.blugrid.security.core.session

import net.blugrid.security.core.model.BaseAuthenticatedSession

data class GuestSession(
    override val sessionId: String,
    override val userId: String,
    override val webApplicationId: String
) : BaseAuthenticatedSession {
    override val sessionType: SessionType = SessionType.GUEST
}
