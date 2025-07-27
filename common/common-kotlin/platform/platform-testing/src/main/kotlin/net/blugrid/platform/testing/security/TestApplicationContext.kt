package net.blugrid.platform.testing.security

import net.blugrid.security.core.model.AuthenticatedOrganisation
import net.blugrid.security.core.model.AuthenticatedUser
import net.blugrid.security.core.model.AuthenticationType
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.TenantSession
import net.blugrid.security.tokens.factory.AccessTokenFactory
import net.blugrid.security.tokens.model.JwtToken

object TestApplicationContext {

    fun configureTenantApplicationContext(
        tenantId: Long = 1L,
        sessionId: Long = 1L,
        userIdentityId: Long = 1L,
        webApplicationId: Long = 1L,
        operatorId: Long = 1L
    ) {
        val organisation = setupTenantToken(tenantId)
        val session = setupWebApplicationSessionToken(tenantId, sessionId, userIdentityId, webApplicationId, operatorId)
        val user = setupUserToken(userIdentityId)
        setupTenantJwtToken(organisation, session, user)
    }

    fun configureBusinessUnitApplicationContext(
        tenantId: Long = 1L,
        sessionId: Long = 1L,
        userIdentityId: Long = 1L,
        webApplicationId: Long = 1L,
        operatorId: Long = 1L,
        businessUnitId: Long = 1L
    ) {
        val organisation = setupTenantToken(tenantId)
        val session = setupBusinessUnitSessionToken(tenantId, sessionId, userIdentityId, webApplicationId, operatorId, businessUnitId)
        val user = setupUserToken(userIdentityId)
        setupBusinessUnitJwtToken(organisation, session, user)

    }

    private fun setupTenantJwtToken(
        organisation: AuthenticatedOrganisation,
        session: BaseAuthenticatedSession,
        user: AuthenticatedUser,
    ) {
        TestAuthFilter.Companion.JWT_TOKEN = AccessTokenFactory.token(
            JwtToken(
                authenticationType = AuthenticationType.TENANT,
                user = user,
                organisation = organisation,
                session = session
            )
        )
    }

    private fun setupBusinessUnitJwtToken(
        organisation: AuthenticatedOrganisation,
        session: BusinessUnitSession,
        user: AuthenticatedUser,
    ) {
        TestAuthFilter.Companion.JWT_TOKEN = AccessTokenFactory.token(
            JwtToken(
                authenticationType = AuthenticationType.BUSINESS_UNIT,
                organisation = organisation,
                user = user,
                session = session,
            )
        )
    }


    private fun setupTenantToken(tenantId: Long) = AuthenticatedOrganisation(
        tenantId = tenantId.toString(),
        displayName = "Test Client",
    )

    private fun setupWebApplicationSessionToken(
        tenantId: Long,
        sessionId: Long,
        userIdentityId: Long,
        webApplicationId: Long,
        operatorId: Long
    ) = TenantSession(
        sessionId = sessionId.toString(),
        tenantId = tenantId.toString(),
        userId = userIdentityId.toString(),
        webApplicationId = webApplicationId.toString(),
        operatorId = operatorId.toString(),
    )

    private fun setupBusinessUnitSessionToken(
        tenantId: Long,
        sessionId: Long,
        userIdentityId: Long,
        webApplicationId: Long,
        operatorId: Long,
        businessUnitId: Long
    ) = BusinessUnitSession(
        sessionId = sessionId.toString(),
        tenantId = tenantId.toString(),
        userId = userIdentityId.toString(),
        webApplicationId = webApplicationId.toString(),
        operatorId = operatorId.toString(),
        businessUnitId = businessUnitId.toString(),
    )

    private fun setupUserToken(userIdentityId: Long) = AuthenticatedUser(
        userIdentityId = userIdentityId.toString(),
        displayName = "Test User",
        email = "tester@test.com",
        providerId = "52565265423"
    )
}
