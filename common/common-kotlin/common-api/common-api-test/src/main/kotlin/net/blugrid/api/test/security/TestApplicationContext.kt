package net.blugrid.api.test.security

import net.blugrid.api.security.authentication.model.AuthenticatedBusinessUnitSession
import net.blugrid.api.security.authentication.model.AuthenticatedOrganisation
import net.blugrid.api.security.authentication.model.AuthenticatedUser
import net.blugrid.api.security.authentication.model.AuthenticatedWebApplicationSession
import net.blugrid.api.security.authentication.model.AuthenticationType
import net.blugrid.api.security.jwt.factory.AccessTokenFactory
import net.blugrid.api.security.jwt.model.JwtToken
import net.blugrid.api.test.security.TestAuthFilter.Companion

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
        session: AuthenticatedWebApplicationSession,
        user: AuthenticatedUser,
    ) {
        Companion.JWT_TOKEN = AccessTokenFactory.token(
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
        session: AuthenticatedBusinessUnitSession,
        user: AuthenticatedUser,
    ) {
        Companion.JWT_TOKEN = AccessTokenFactory.token(
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
    ) = AuthenticatedWebApplicationSession(
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
    ) = AuthenticatedBusinessUnitSession(
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
