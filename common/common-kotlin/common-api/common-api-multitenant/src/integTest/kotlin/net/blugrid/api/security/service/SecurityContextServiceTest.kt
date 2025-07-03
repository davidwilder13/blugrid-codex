package net.blugrid.api.security.service

import io.github.serpro69.kfaker.faker
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import net.blugrid.api.common.model.organisation.OrganisationCreate
import net.blugrid.api.example.model.BookCreate
import net.blugrid.api.example.service.BookStateServiceImpl
import net.blugrid.api.organisation.service.OrganisationStateServiceImpl
import net.blugrid.api.security.context.doInRequestContext
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

@MicronautTest(environments = ["logging", "json", "security", "db", "multitenant-test"])
class SecurityContextServiceTest {

    @Inject
    lateinit var securityContextService: SecurityContextServiceImpl

    @Inject
    lateinit var bookStateService: BookStateServiceImpl

    @Inject
    lateinit var organisationStateService: OrganisationStateServiceImpl

    val faker = faker {}

    @Test
    fun `test runWithTenantId saves to the database with the assumed tenantId`() {
        val firstTenantId = 1L
        val secondTenantId = 2L
        doInRequestContext {
            securityContextService.runWithTenantId(firstTenantId) {
                bookStateService.create(book())
                    .also { assertThat("run with first tenantId", it.permission!!.tenantId, equalTo(firstTenantId)) }

                organisationStateService.create(OrganisationCreate(uuid = UUID.randomUUID(), parentOrganisationId = -1))

                securityContextService.runWithTenantId(secondTenantId) {
                    bookStateService.create(book())
                        .also { assertThat("run with second tenantId", it.permission!!.tenantId, equalTo(secondTenantId)) }
                }

                bookStateService.create(book())
                    .also { assertThat("run with first tenantId again", it.permission!!.tenantId, equalTo(firstTenantId)) }
            }
        }
    }

    private fun book() = BookCreate(uuid = UUID.randomUUID(), name = faker.cannabis.strains())
}
