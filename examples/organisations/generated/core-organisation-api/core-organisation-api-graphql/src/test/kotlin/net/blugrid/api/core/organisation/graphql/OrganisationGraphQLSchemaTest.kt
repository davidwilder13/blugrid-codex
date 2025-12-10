package net.blugrid.api.core.organisation.graphql

import com.expediagroup.graphql.generator.scalars.ID
import net.blugrid.api.core.organisation.graphql.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Unit tests for GraphQL types and model objects - does NOT require Docker/Testcontainers.
 */
@DisplayName("OrganisationGraphQL Model Tests")
class OrganisationGraphQLSchemaTest {

    @Test
    fun `OrganisationType can be created with required fields`() {
        val org = OrganisationType(
            id = ID("1"),
            uuid = "550e8400-e29b-41d4-a716-446655440000",
            parentOrganisationId = 100L,
            effectiveTimestamp = "2025-12-10T10:00:00"
        )

        assertEquals("1", org.id.value)
        assertEquals("550e8400-e29b-41d4-a716-446655440000", org.uuid)
        assertEquals(100L, org.parentOrganisationId)
        assertEquals("2025-12-10T10:00:00", org.effectiveTimestamp)
    }

    @Test
    fun `OrganisationCreateInput requires mandatory fields`() {
        val input = OrganisationCreateInput(
            parentOrganisationId = 200L,
            effectiveTimestamp = "2025-12-10T12:00:00"
        )

        assertEquals(200L, input.parentOrganisationId)
        assertEquals("2025-12-10T12:00:00", input.effectiveTimestamp)
    }

    @Test
    fun `OrganisationUpdateInput has all optional fields`() {
        val input = OrganisationUpdateInput(
            parentOrganisationId = 300L,
            effectiveTimestamp = "2025-12-10T14:00:00"
        )

        assertEquals(300L, input.parentOrganisationId)
        assertEquals("2025-12-10T14:00:00", input.effectiveTimestamp)
    }

    @Test
    fun `OrganisationUpdateInput allows null values`() {
        val input = OrganisationUpdateInput()

        assertNull(input.parentOrganisationId)
        assertNull(input.effectiveTimestamp)
    }

    @Test
    fun `OrganisationConnection contains Relay pagination structure`() {
        val org = OrganisationType(
            id = ID("1"),
            uuid = "550e8400-e29b-41d4-a716-446655440000",
            parentOrganisationId = 100L,
            effectiveTimestamp = "2025-12-10T10:00:00"
        )

        val edge = OrganisationEdge(node = org, cursor = "cursor-1")
        val pageInfo = PageInfo(
            hasNextPage = true,
            hasPreviousPage = false,
            startCursor = "cursor-1",
            endCursor = "cursor-1"
        )
        val connection = OrganisationConnection(
            edges = listOf(edge),
            pageInfo = pageInfo,
            totalCount = 1
        )

        assertEquals(1, connection.edges.size)
        assertEquals("cursor-1", connection.edges[0].cursor)
        assertEquals(org, connection.edges[0].node)
        assertTrue(connection.pageInfo.hasNextPage)
        assertFalse(connection.pageInfo.hasPreviousPage)
        assertEquals(1, connection.totalCount)
    }

    @Test
    fun `ID scalar wraps string correctly`() {
        val id = ID("test-id-123")
        assertEquals("test-id-123", id.value)
    }

    @Test
    fun `OrganisationQueries class exists with required methods`() {
        val queriesClass = OrganisationQueries::class
        assertNotNull(queriesClass)
        assertTrue(queriesClass.java.declaredMethods.any { it.name == "organisation" })
        assertTrue(queriesClass.java.declaredMethods.any { it.name == "organisations" })
        assertTrue(queriesClass.java.declaredMethods.any { it.name == "allOrganisations" })
    }

    @Test
    fun `OrganisationMutations class exists with required methods`() {
        val mutationsClass = OrganisationMutations::class
        assertNotNull(mutationsClass)
        assertTrue(mutationsClass.java.declaredMethods.any { it.name == "createOrganisation" })
        assertTrue(mutationsClass.java.declaredMethods.any { it.name == "updateOrganisation" })
        assertTrue(mutationsClass.java.declaredMethods.any { it.name == "deleteOrganisation" })
    }

    @Test
    fun `OrganisationFederationResolver class exists`() {
        val resolverClass = OrganisationFederationResolver::class
        assertNotNull(resolverClass)
    }

    @Test
    fun `OrganisationDataLoaderFactory exists and provides data loader name`() {
        val factoryClass = OrganisationDataLoaderFactory::class
        assertNotNull(factoryClass)
        assertEquals("OrganisationDataLoader", OrganisationDataLoaderFactory.DATA_LOADER_NAME)
    }
}
