package net.blugrid.api.core.organisation.service

import jakarta.inject.Inject
import net.blugrid.api.core.organisation.assertion.assert
import net.blugrid.api.core.organisation.assertion.assertEqualTo
import net.blugrid.api.core.organisation.factory.organisationCreate
import net.blugrid.api.core.organisation.factory.organisationUpdate
import net.blugrid.api.core.organisation.repository.OrganisationRepository
import net.blugrid.api.test.security.TestApplicationContext
import net.blugrid.api.test.support.BaseServiceIntegTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Optional

@DisplayName("OrganisationStateServiceDbImpl")
class OrganisationStateServiceDbImplIntegTest : BaseServiceIntegTest() {

    @Inject
    lateinit var service: OrganisationCommandService

    @Inject
    lateinit var repository: OrganisationRepository

    @BeforeEach
    fun setup() {
        TestApplicationContext.configureTenantApplicationContext()
    }

    @Test
    fun `create organisation`() {
        val result = service.create(organisationCreate())
        result.assert(id = result.id)
    }

    @Test
    fun `update organisation`() {
        val created = service.create(organisationCreate())
        val updated = service.update(created.id, organisationUpdate {
            id = created.id
            uuid = created.uuid
            parentOrganisationId = 123L
        })
        updated.assert(
            id = created.id,
            uuid = created.uuid,
            parentOrganisationId = 123L
        )
    }

    @Test
    fun `get organisation by id`() {
        val created = service.create(organisationCreate())
        val found = service.getById(created.id)
        found.assertEqualTo(created)
    }

    @Test
    fun `delete organisation`() {
        val created = service.create(organisationCreate())
        service.delete(created.id)
        val found = repository.findById(created.id)
        MatcherAssert.assertThat(found, CoreMatchers.equalTo(Optional.empty()))
    }
}
