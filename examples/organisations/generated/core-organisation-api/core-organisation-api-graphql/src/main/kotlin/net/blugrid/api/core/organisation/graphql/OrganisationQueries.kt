package net.blugrid.api.core.organisation.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import jakarta.inject.Singleton
import net.blugrid.api.core.organisation.graphql.model.OrganisationConnection
import net.blugrid.api.core.organisation.graphql.model.OrganisationEdge
import net.blugrid.api.core.organisation.graphql.model.OrganisationType
import net.blugrid.api.core.organisation.graphql.model.PageInfo
import net.blugrid.api.core.organisation.graphql.model.toGraphQL
import net.blugrid.api.core.organisation.grpc.client.OrganisationGrpcClient
import net.blugrid.api.core.organisation.grpc.organisationPageRequest
import java.util.Base64

@Singleton
class OrganisationQueries(
    private val grpcClient: OrganisationGrpcClient
) : Query {

    @GraphQLDescription("Get a single organisation by ID")
    suspend fun organisation(id: Long): OrganisationType? {
        val response = grpcClient.getByIdOptional(id)
        return if (response.exists) {
            response.organisation.toGraphQL()
        } else {
            null
        }
    }

    @GraphQLDescription("Get a paginated list of organisations")
    suspend fun organisations(
        first: Int = 20,
        after: String? = null
    ): OrganisationConnection {
        val page = after?.let { decodeCursor(it) } ?: 0

        val request = organisationPageRequest {
            this.page = page
            this.size = first
        }

        val response = grpcClient.getPage(request)
        val organisations = response.organisationsList.map { it.toGraphQL() }

        val edges = organisations.mapIndexed { index, org ->
            OrganisationEdge(
                node = org,
                cursor = encodeCursor(page * first + index)
            )
        }

        return OrganisationConnection(
            edges = edges,
            pageInfo = PageInfo(
                hasNextPage = (page + 1) < response.totalPages,
                hasPreviousPage = page > 0,
                startCursor = edges.firstOrNull()?.cursor,
                endCursor = edges.lastOrNull()?.cursor
            ),
            totalCount = response.totalElements
        )
    }

    @GraphQLDescription("Get all organisations (use with caution)")
    suspend fun allOrganisations(): List<OrganisationType> {
        return grpcClient.getAll().organisationsList.map { it.toGraphQL() }
    }

    private fun encodeCursor(offset: Int): String {
        return Base64.getEncoder().encodeToString("cursor:$offset".toByteArray())
    }

    private fun decodeCursor(cursor: String): Int {
        val decoded = String(Base64.getDecoder().decode(cursor))
        return decoded.removePrefix("cursor:").toIntOrNull() ?: 0
    }
}
