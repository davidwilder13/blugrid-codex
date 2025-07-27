package net.blugrid.security.authentication.model

import io.micronaut.security.authentication.Authentication
import net.blugrid.security.core.model.AuthenticatedUser
import net.blugrid.security.core.model.AuthenticationType
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.GuestSession
import java.util.Date

data class GuestAuthentication(
    override val providerId: String,
    override val principalName: String,
    override val principalEmail: String,
    override val sessionId: String,
    override val userId: String,
    override val expirationTime: Date? = null,
    override val user: AuthenticatedUser,
    override val session: GuestSession,
) : DecoratedAuthentication<GuestSession>, Authentication {
    override val authenticationType: AuthenticationType = AuthenticationType.GUEST

    override fun getAttributes(): MutableMap<String, Any> = mutableMapOf(
        "userId" to user.userIdentityId,
        "sessionId" to session.sessionId,
        "webApplicationId" to session.webApplicationId,
        "authenticationType" to authenticationType.name,
        // Add this for CurrentRequestContext access
        "user" to user
    )

    override fun getName(): String = principalName
}
