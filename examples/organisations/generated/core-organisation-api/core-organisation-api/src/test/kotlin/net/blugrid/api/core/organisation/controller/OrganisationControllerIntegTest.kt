package net.blugrid.api.core.organisation.controller

import net.blugrid.api.core.organisation.factory.OrganisationCreateFactory
import net.blugrid.api.core.organisation.factory.OrganisationUpdateFactory
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.common.model.pagination.Pageable
import net.blugrid.common.model.pagination.Sort
import net.blugrid.common.model.pagination.SortDirection
import net.blugrid.common.model.pagination.SortOrder
import net.blugrid.platform.testing.security.TestApplicationContext
import net.blugrid.platform.testing.support.BaseControllerIntegTest
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
            createPayload = OrganisationCreateFactory.create(),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `update Organisation`() {
        val created = assertCreate(OrganisationCreateFactory.create(), Organisation::class.java)
        val updateModel = OrganisationUpdateFactory.from(created) {
            parentOrganisationId = 123L
        }

        assertUpdate(updatePayload = updateModel, responseType = Organisation::class.java)
    }

    @Test
    fun `get Organisation by Id`() {
        assertGetById(
            createPayload = OrganisationCreateFactory.create(),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `get Organisation page`() {
        val resources = List(3) { OrganisationCreateFactory.create() }
        assertGetPage(
            resources = resources,
            pageable = Pageable.from(0, 25, Sort.by(SortOrder("id", SortDirection.ASC, false))),
            responseType = Organisation::class.java,
        )
    }

    @Test
    fun `delete Organisation`() {
        assertDelete(
            createPayload = OrganisationCreateFactory.create(),
            responseType = Organisation::class.java,
        )
    }
}
