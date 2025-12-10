package net.blugrid.api.core.organisation.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import jakarta.inject.Singleton
import net.blugrid.api.core.organisation.graphql.model.OrganisationCreateInput
import net.blugrid.api.core.organisation.graphql.model.OrganisationType
import net.blugrid.api.core.organisation.graphql.model.OrganisationUpdateInput
import net.blugrid.api.core.organisation.graphql.model.toGraphQL
import net.blugrid.api.core.organisation.grpc.client.OrganisationGrpcClient
import net.blugrid.api.core.organisation.grpc.organisationCreateRequest
import net.blugrid.api.core.organisation.grpc.organisationUpdateRequest
import java.util.UUID

@Singleton
class OrganisationMutations(
    private val grpcClient: OrganisationGrpcClient
) : Mutation {

    @GraphQLDescription("Create a new organisation")
    suspend fun createOrganisation(input: OrganisationCreateInput): OrganisationType {
        val request = organisationCreateRequest {
            uuid = UUID.randomUUID().toString()
            parentOrganisationId = input.parentOrganisationId
            effectiveTimestamp = input.effectiveTimestamp
        }

        val response = grpcClient.create(request)
        return response.toGraphQL()
    }

    @GraphQLDescription("Update an existing organisation")
    suspend fun updateOrganisation(id: Long, input: OrganisationUpdateInput): OrganisationType {
        // First get the existing organisation to merge updates
        val existing = grpcClient.getById(id)

        val request = organisationUpdateRequest {
            this.id = id
            uuid = existing.uuid
            parentOrganisationId = input.parentOrganisationId ?: existing.parentOrganisationId
            effectiveTimestamp = input.effectiveTimestamp ?: existing.effectiveTimestamp
        }

        val response = grpcClient.update(request)
        return response.toGraphQL()
    }

    @GraphQLDescription("Delete an organisation")
    suspend fun deleteOrganisation(id: Long): Boolean {
        grpcClient.delete(id)
        return true
    }
}
