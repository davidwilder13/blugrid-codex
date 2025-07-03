package net.blugrid.api.core.organisation.controller

import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.data.model.Sort.Order
import io.micronaut.data.model.Sort.Order.Direction.ASC
import net.blugrid.api.core.organisation.factory.organisationCreate
import net.blugrid.api.core.organisation.factory.organisationUpdate
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.test.security.TestApplicationContext
import net.blugrid.api.test.support.BaseControllerIntegTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("OrganisationController")
class OrganisationControllerIntegTest : BaseControllerIntegTest("/organisations") {

    @BeforeEach
    fun setup() {
        TestApplicationContext.configureTenantApplicationContext()
    }

    @Test
    fun `create Organisation`() {
        assertCreate(
            createPayload = organisationCreate(),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `update Organisation`() {
        val created = assertCreate(organisationCreate(), Organisation::class.java)
        val update = organisationUpdate {
            id = created.id
            uuid = created.uuid
            parentOrganisationId = 123L
        }

        assertUpdate(updatePayload = update, responseType = Organisation::class.java)
    }

    @Test
    fun `get Organisation by Id`() {
        assertGetById(
            createPayload = organisationCreate(),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `get Organisation page`() {
        val resources = List(3) { organisationCreate() }
        assertGetPage(
            resources = resources,
            pageable = Pageable.from(0, 25, Sort.of(Order("id", ASC, false))),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `delete Organisation`() {
        assertDelete(
            createPayload = organisationCreate(),
            responseType = Organisation::class.java,
        )
    }
}
