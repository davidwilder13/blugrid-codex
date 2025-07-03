package net.blugrid.api.core.organisation.service

import jakarta.inject.Inject
import net.blugrid.api.core.organisation.assertion.assert
import net.blugrid.api.core.organisation.assertion.assertEqualTo
import net.blugrid.api.core.organisation.factory.organisationCreate
import net.blugrid.api.core.organisation.factory.organisationUpdate
import net.blugrid.api.core.organisation.repository.OrganisationRepository
import net.blugrid.api.security.context.doInRequestContext
import net.blugrid.api.security.service.SecurityContextService
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
    lateinit var securityContextService: SecurityContextService

    @Inject
    lateinit var commandService: OrganisationCommandService

    @Inject
    lateinit var queryService: OrganisationQueryService

    @Inject
    lateinit var repository: OrganisationRepository

    @BeforeEach
    fun setup() {
        TestApplicationContext.configureTenantApplicationContext()
    }

    @Test
    fun `create organisation`() {
        doInRequestContext {
            securityContextService.runWithTenantId(1L) {
                val result = commandService.create(organisationCreate())
                result.assert(id = result.id.value)
            }
        }
    }

    @Test
    fun `update organisation`() {
        val created = commandService.create(organisationCreate())
        val updated = commandService.update(created.id.value, organisationUpdate {
            id = created.id
            uuid = created.uuid
            parentOrganisationId = 123L
        })
        updated.assert(
            id = created.id.value,
            uuid = created.uuid.value,
            parentOrganisationId = 123L
        )
    }

    @Test
    fun `get organisation by id`() {
        val created = commandService.create(organisationCreate())
        val found = queryService.getById(created.id.value)
        found.assertEqualTo(created)
    }

    @Test
    fun `delete organisation`() {
        val created = commandService.create(organisationCreate())
        commandService.delete(created.id.value)
        val found = repository.findById(created.id.value)
        MatcherAssert.assertThat(found, CoreMatchers.equalTo(Optional.empty()))
    }
}
