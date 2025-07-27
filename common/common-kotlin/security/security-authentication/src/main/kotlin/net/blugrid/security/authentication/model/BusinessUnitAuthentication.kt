package net.blugrid.security.authentication.model

import io.micronaut.security.authentication.Authentication
import net.blugrid.security.core.model.AuthenticatedOrganisation
import net.blugrid.security.core.model.AuthenticatedUser
import net.blugrid.security.core.model.AuthenticationType
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.BusinessUnitSession
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

    override fun getAttributes(): MutableMap<String, Any> = mutableMapOf(
        "userId" to user.userIdentityId,
        "sessionId" to session.sessionId,
        "tenantId" to session.tenantId,
        "businessUnitId" to session.businessUnitId,
        "webApplicationId" to session.webApplicationId,
        "authenticationType" to authenticationType.name,
        // Add these for CurrentRequestContext access
        "user" to user,
        "organisation" to organisation
    )

    override fun getName(): String = principalName
}
