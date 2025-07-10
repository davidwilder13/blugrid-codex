package net.blugrid.api.security.support

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import net.blugrid.api.organisation.model.Organisation
import net.blugrid.api.security.model.AuthenticatedOrganisation
import net.blugrid.api.security.model.AuthenticatedUser
import net.blugrid.api.security.model.BusinessUnitAuthentication
import net.blugrid.api.security.model.GuestAuthentication
import net.blugrid.api.security.model.TenantAuthentication
import net.blugrid.api.session.model.BusinessUnitSession
import net.blugrid.api.session.model.GuestSession
import net.blugrid.api.session.model.TenantSession
import net.blugrid.api.userIdentity.model.UserIdentity
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseSecurityTest {

    protected fun createTestUserIdentity(
        id: Long = 1L,
        email: String = "test@example.com",
        displayName: String = "Test User"
    ): UserIdentity = object : UserIdentity {
        override val id: Long = id
        override val uuid: UUID = UUID.randomUUID()
        override val name: String = displayName
        override val email: String = email
        override val displayName: String? = displayName
        override val emailVerified: Boolean? = true
        override val providerId: String = "auth0"
        override val partyId: Long? = null
        override val nickName: String? = null
        override val givenName: String? = "Test"
        override val familyName: String? = "User"
        override val pictureUrl: String? = null
    }

    protected fun createTestOrganisation(
        tenantId: String = "tenant123"
    ): Organisation = object : Organisation {
        override val id: Long = tenantId.toLongOrNull() ?: 123L
        override val displayName: String? = "Test Organisation"
        override val partyId: Long? = null
        override val primaryPartyId: Long? = null
    }

    protected fun createTestAuthenticatedUser(
        userIdentityId: String = "1"
    ): AuthenticatedUser = AuthenticatedUser(
        userIdentityId = userIdentityId,
        displayName = "Test User",
        email = "test@example.com",
        emailVerified = true,
        partyId = "1",
        providerId = "auth0",
        nickName = null,
        givenName = "Test",
        familyName = "User",
        pictureUrl = null
    )

    protected fun createTestAuthenticatedOrganisation(
        tenantId: String = "tenant123"
    ): AuthenticatedOrganisation = AuthenticatedOrganisation(
        tenantId = tenantId,
        displayName = "Test Organisation",
        partyId = null,
        primaryPartyId = null
    )

    protected fun createTestGuestSession(
        sessionId: String = "session123"
    ): GuestSession = GuestSession(
        sessionId = sessionId,
        userId = "user456",
        webApplicationId = "100001"
    )

    protected fun createTestTenantSession(
        sessionId: String = "session123",
        tenantId: String = "tenant123"
    ): TenantSession = TenantSession(
        sessionId = sessionId,
        userId = "user456",
        tenantId = tenantId,
        webApplicationId = "100001",
        operatorId = "operator789"
    )

    protected fun createTestBusinessUnitSession(
        sessionId: String = "session123",
        tenantId: String = "tenant123",
        businessUnitId: String = "bu456"
    ): BusinessUnitSession = BusinessUnitSession(
        sessionId = sessionId,
        userId = "user456",
        tenantId = tenantId,
        businessUnitId = businessUnitId,
        webApplicationId = "100001",
        operatorId = "operator789"
    )

    protected fun createGuestAuthentication(
        sessionId: String = "session123",
        userId: String = "user456"
    ): GuestAuthentication = GuestAuthentication(
        providerId = "auth0",
        principalName = "guest_user",
        principalEmail = "guest@example.com",
        sessionId = sessionId,
        userId = userId,
        user = createTestAuthenticatedUser(userId),
        session = createTestGuestSession(sessionId)
    )

    protected fun createTenantAuthentication(
        tenantId: String = "tenant123",
        sessionId: String = "session123"
    ): TenantAuthentication = TenantAuthentication(
        providerId = "auth0",
        principalName = "tenant_user",
        principalEmail = "user@tenant.com",
        sessionId = sessionId,
        userId = "user456",
        organisation = createTestAuthenticatedOrganisation(tenantId),
        session = createTestTenantSession(sessionId, tenantId),
        user = createTestAuthenticatedUser()
    )

    protected fun createBusinessUnitAuthentication(
        tenantId: String = "tenant123",
        businessUnitId: String = "bu456",
        sessionId: String = "session123"
    ): BusinessUnitAuthentication = BusinessUnitAuthentication(
        providerId = "auth0",
        principalName = "bu_user",
        principalEmail = "user@businessunit.com",
        sessionId = sessionId,
        userId = "user456",
        organisation = createTestAuthenticatedOrganisation(tenantId),
        session = createTestBusinessUnitSession(sessionId, tenantId, businessUnitId),
        user = createTestAuthenticatedUser()
    )
}
