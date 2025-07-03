package net.blugrid.api.security.model

import io.micronaut.security.authentication.Authentication
import net.blugrid.api.session.model.BusinessUnitSession
import java.util.Date

data class BusinessUnitAuthentication(
    override val providerId: String,
    override val principalName: String,
    override val principalEmail: String,
    override val sessionId: String,
    override val userId: String,
    override val expirationTime: Date? = null,
    override val user: AuthenticatedUser,
    override val session: BusinessUnitSession,
    val organisation: AuthenticatedOrganisation,
) : DecoratedAuthentication<BusinessUnitSession>, Authentication {
    override val authenticationType: AuthenticationType = AuthenticationType.BUSINESS_UNIT

    override fun getName(): String = principalName

    override fun getAttributes(): Map<String, Any> = mapOf(
        "userId" to user.userIdentityId,
        "sessionId" to session.sessionId,
        "tenantId" to organisation.tenantId,
        "businessUnitId" to session.businessUnitId,
        "webApplicationId" to session.webApplicationId,
        "authenticationType" to authenticationType.name
    )
}
