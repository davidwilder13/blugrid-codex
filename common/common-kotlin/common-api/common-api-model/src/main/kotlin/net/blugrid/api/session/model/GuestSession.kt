package net.blugrid.api.session.model

import net.blugrid.api.security.model.BaseAuthenticatedSession

data class GuestSession(
    override val sessionId: String,
    override val userId: String,
    override val webApplicationId: String
) : BaseAuthenticatedSession {
    override val sessionType: SessionType = SessionType.GUEST
}
