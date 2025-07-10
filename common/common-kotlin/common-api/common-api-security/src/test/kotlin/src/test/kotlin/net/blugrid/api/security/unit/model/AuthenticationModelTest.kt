package src.test.kotlin.net.blugrid.api.security.unit.model

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import net.blugrid.api.security.model.AuthenticationType
import net.blugrid.api.security.support.BaseSecurityTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationModelTest : BaseSecurityTest() {

    @Test
    fun `guest authentication should contain minimal required fields`() {
        val guestAuth = createGuestAuthentication()

        assertThat(guestAuth.authenticationType).isEqualTo(AuthenticationType.GUEST)
        assertThat(guestAuth.principalName).isEqualTo("guest_user")
        assertThat(guestAuth.principalEmail).isEqualTo("guest@example.com")
        assertThat(guestAuth.sessionId).isEqualTo("session123")
        assertThat(guestAuth.userId).isEqualTo("user456")
        assertThat(guestAuth.user).isNotNull()
        assertThat(guestAuth.session).isNotNull()
    }

    @Test
    fun `tenant authentication should contain organisation information`() {
        val tenantAuth = createTenantAuthentication()

        assertThat(tenantAuth.authenticationType).isEqualTo(AuthenticationType.TENANT)
        assertThat(tenantAuth.organisation).isNotNull()
        assertThat(tenantAuth.organisation.tenantId).isEqualTo("tenant123")
        assertThat(tenantAuth.organisation.displayName).isEqualTo("Test Organisation")
    }

    @Test
    fun `business unit authentication should contain business unit information`() {
        val businessUnitAuth = createBusinessUnitAuthentication()

        assertThat(businessUnitAuth.authenticationType).isEqualTo(AuthenticationType.BUSINESS_UNIT)
        assertThat(businessUnitAuth.organisation).isNotNull()
        assertThat(businessUnitAuth.session.businessUnitId).isEqualTo("bu456")
        assertThat(businessUnitAuth.session.tenantId).isEqualTo("tenant123")
    }

    @Test
    fun `authentication attributes should contain correct keys`() {
        val guestAuth = createGuestAuthentication()
        val tenantAuth = createTenantAuthentication()
        val businessUnitAuth = createBusinessUnitAuthentication()

        // Guest attributes
        assertThat(guestAuth.attributes).containsKeys("userId", "sessionId", "webApplicationId", "authenticationType")
        assertThat(guestAuth.attributes).doesNotContainKey("tenantId")
        assertThat(guestAuth.attributes).doesNotContainKey("businessUnitId")

        // Tenant attributes
        assertThat(tenantAuth.attributes).containsKeys("userId", "sessionId", "tenantId", "webApplicationId", "authenticationType")
        assertThat(tenantAuth.attributes).doesNotContainKey("businessUnitId")

        // Business unit attributes
        assertThat(businessUnitAuth.attributes).containsKeys("userId", "sessionId", "tenantId", "businessUnitId", "webApplicationId", "authenticationType")
    }
}
