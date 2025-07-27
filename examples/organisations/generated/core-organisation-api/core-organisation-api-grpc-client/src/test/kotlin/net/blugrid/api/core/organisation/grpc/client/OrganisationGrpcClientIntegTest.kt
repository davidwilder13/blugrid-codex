package net.blugrid.api.core.organisation.grpc.client

import net.blugrid.api.core.organisation.grpc.toOrganisationCreateRequest
import net.blugrid.api.core.organisation.grpc.toOrganisationUpdateRequest
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import net.blugrid.platform.testing.security.TestApplicationContext
import net.blugrid.platform.testing.support.BaseGrpcClientIntegTest
import net.blugrid.platform.testing.support.GrpcServerTestSupport
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("OrganisationGrpcClient")
class OrganisationGrpcClientIntegTest : BaseGrpcClientIntegTest() {

    private val client by lazy {
        val host = GrpcServerTestSupport.externalHost
        val port = GrpcServerTestSupport.externalPort
        OrganisationGrpcClientFactory()
            .create(host, port)
    }

    @BeforeEach
    fun init() {
        TestApplicationContext.configureTenantApplicationContext()
    }

    @Test
    fun `create and fetch Organisation by ID`() = runGrpcTest {
        val createReq = OrganisationCreate(
            uuid = IdentityUUID(UUID.randomUUID()),
            parentOrganisationId = 101,
            effectiveTimestamp = LocalDateTime.parse("2025-01-01T00:00:00")
        )
        val created = client.create(createReq.toOrganisationCreateRequest())
        assertEquals(101, created.parentOrganisationId)

        val fetched = client.getById(created.id)
        assertEquals(created.id, fetched.id)
        assertEquals(created.parentOrganisationId, fetched.parentOrganisationId)
    }

    @Test
    fun `update Organisation`() = runGrpcTest {
        val createReq = OrganisationCreate(
            uuid = IdentityUUID(UUID.randomUUID()),
            parentOrganisationId = 500,
            effectiveTimestamp = LocalDateTime.now()
        )
        val created = client.create(createReq.toOrganisationCreateRequest())

        val updateReq = OrganisationUpdate(
            id = IdentityID(created.id),
            uuid = IdentityUUID(UUID.fromString(created.uuid)),
            parentOrganisationId = 777,
            effectiveTimestamp = LocalDateTime.now().plusDays(1)
        )

        val updated = client.update(updateReq.toOrganisationUpdateRequest())
        assertEquals(777, updated.parentOrganisationId)
        assertEquals(created.id, updated.id)
    }

    @Test
    fun `get Organisation by UUID`() = runGrpcTest {
        val createReq = OrganisationCreate(
            uuid = IdentityUUID(UUID.randomUUID()),
            parentOrganisationId = 200,
            effectiveTimestamp = LocalDateTime.now()
        )
        val created = client.create(createReq.toOrganisationCreateRequest())

        val fetched = client.getByUuid(UUID.fromString(created.uuid))
        assertEquals(created.id, fetched.id)
    }

    @Test
    fun `getAll returns created Organisation`() = runGrpcTest {
        val created = client.create(
            OrganisationCreate(
                uuid = IdentityUUID(UUID.randomUUID()),
                parentOrganisationId = 321,
                effectiveTimestamp = LocalDateTime.now()
            ).toOrganisationCreateRequest()
        )

        val all = client.getAll().organisationsList
        assertTrue(all.any { it.id == created.id })
    }

    @Test
    fun `delete Organisation`() = runGrpcTest {
        val created = client.create(
            OrganisationCreate(
                uuid = IdentityUUID(UUID.randomUUID()),
                parentOrganisationId = 404,
                effectiveTimestamp = LocalDateTime.now()
            ).toOrganisationCreateRequest()
        )

        client.delete(created.id)

        val ex = runCatching {
            client.getById(created.id)
        }.exceptionOrNull()

        assertNotNull(ex)
        assertTrue(ex?.message?.contains("NOT_FOUND") == true)
    }
}
