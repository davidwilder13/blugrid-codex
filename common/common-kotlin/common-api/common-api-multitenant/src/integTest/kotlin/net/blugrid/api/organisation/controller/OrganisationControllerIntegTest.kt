package net.blugrid.api.organisation.controller

import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.data.model.Sort.Order
import io.micronaut.data.model.Sort.Order.Direction.DESC
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import net.blugrid.api.BaseMultitenantIntegTest
import net.blugrid.api.common.model.organisation.Organisation
import net.blugrid.api.common.model.organisation.OrganisationCreate
import net.blugrid.api.common.model.organisation.OrganisationUpdate
import net.blugrid.api.organisation.mapper.OrganisationMapper
import net.blugrid.api.test.security.TestApplicationContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("Organisation")
class OrganisationControllerIntegTest(
    private val mapper: OrganisationMapper,
    @Client("/") override val client: HttpClient
) : BaseMultitenantIntegTest(
    baseUri = OrganisationController.PATH,
    client = client
) {

    @BeforeEach
    fun setup() {
        TestApplicationContext.configureTenantApplicationContext()
    }

    private fun createOrganisation() =
        OrganisationCreate(
            uuid = UUID.randomUUID(),
            parentOrganisationId = -1,
            effectiveTimestamp = LocalDateTime.now()
        )

    @Test
    fun `create Organisation`() {
        assertCreate(
            createPayload = createOrganisation(),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `update Organisation`() {
        val initialOrganisation = assertCreate(
            createPayload = createOrganisation(),
            responseType = Organisation::class.java,
        )

        assertUpdate(
            updatePayload = OrganisationUpdate(
                id = initialOrganisation.id,
                uuid = initialOrganisation.uuid,
                parentOrganisationId = initialOrganisation.parentOrganisationId,
                effectiveTimestamp = initialOrganisation.effectiveTimestamp,
                primaryPartyId = 123L,
                organisationMembershipId = 456L
            ),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `get Organisation by Id`() {
        assertGetById(
            createPayload = createOrganisation(),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `get Organisation Page`() {
        assertGetPage(
            resources = listOf(createOrganisation(), createOrganisation(), createOrganisation()),
            pageable = Pageable.from(0, 25, Sort.of(Order("displayName", DESC, true))),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `delete Organisation`() {
        assertDelete(
            createPayload = createOrganisation(),
            responseType = Organisation::class.java,
        )
    }
}
