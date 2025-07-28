package net.blugrid.api.core.organisation.service

import jakarta.inject.Inject
import net.blugrid.api.core.organisation.assertion.assert
import net.blugrid.api.core.organisation.assertion.assertEqualTo
import net.blugrid.api.core.organisation.factory.OrganisationCreateFactory
import net.blugrid.api.core.organisation.factory.OrganisationUpdateFactory
import net.blugrid.api.core.organisation.repository.OrganisationRepository
import net.blugrid.platform.testing.security.TestApplicationContext
import net.blugrid.platform.testing.support.BaseServiceIntegTest
import net.blugrid.security.core.context.doInRequestContext
import net.blugrid.security.core.service.SecurityContextService
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
                val createModel = OrganisationCreateFactory.create()
                val result = commandService.create(createModel)
                result.assert(id = result.id.value)
            }
        }
    }

    @Test
    fun `update organisation`() {
        doInRequestContext {
            securityContextService.runWithTenantId(1L) {
                val created = commandService.create(OrganisationCreateFactory.create())
                val updateModel = OrganisationUpdateFactory.from(created) {
                    parentOrganisationId = 123L
                }

                val updated = commandService.update(created.id.value, updateModel)

                updated.assert(
                    id = created.id.value,
                    uuid = created.uuid.value,
                    parentOrganisationId = 123L
                )
            }
        }
    }

    @Test
    fun `get organisation by id`() {
        doInRequestContext {
            securityContextService.runWithTenantId(1L) {
                val created = commandService.create(OrganisationCreateFactory.create())
                val found = queryService.getById(created.id.value)
                found.assertEqualTo(created)
            }
        }
    }

    @Test
    fun `delete organisation`() {
        doInRequestContext {
            securityContextService.runWithTenantId(1L) {
                val created = commandService.create(OrganisationCreateFactory.create())
                commandService.delete(created.id.value)
                val found = repository.findById(created.id.value)
                MatcherAssert.assertThat(found, CoreMatchers.equalTo(Optional.empty()))
            }
        }
    }

    @Test
    fun `create root organisation`() {
        doInRequestContext {
            securityContextService.runWithTenantId(1L) {
                val rootOrg = commandService.create(OrganisationCreateFactory.createRoot())
                rootOrg.assert(parentOrganisationId = -1L)
            }
        }
    }

    @Test
    fun `create child organisation`() {
        doInRequestContext {
            securityContextService.runWithTenantId(1L) {
                val parent = commandService.create(OrganisationCreateFactory.createRoot())
                val child = commandService.create(OrganisationCreateFactory.createChild(parent.id.value))
                child.assert(parentOrganisationId = parent.id.value)
            }
        }
    }
}
