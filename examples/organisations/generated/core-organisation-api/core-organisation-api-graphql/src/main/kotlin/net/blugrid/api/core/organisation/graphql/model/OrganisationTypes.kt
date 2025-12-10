package net.blugrid.api.core.organisation.graphql.model

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.scalars.ID
import net.blugrid.api.core.organisation.grpc.OrganisationResponse
import java.time.LocalDateTime

/**
 * GraphQL type for Organisation - Federation enabled with @key directive
 */
@KeyDirective(fields = FieldSet("id"))
data class OrganisationType(
    val id: ID,
    val uuid: String,
    val parentOrganisationId: Long,
    val effectiveTimestamp: String
)

/**
 * Input type for creating an organisation
 */
data class OrganisationCreateInput(
    val parentOrganisationId: Long,
    val effectiveTimestamp: String
)

/**
 * Input type for updating an organisation
 */
data class OrganisationUpdateInput(
    val parentOrganisationId: Long? = null,
    val effectiveTimestamp: String? = null
)

/**
 * Connection type for paginated results (Relay-style)
 */
data class OrganisationConnection(
    val edges: List<OrganisationEdge>,
    val pageInfo: PageInfo,
    val totalCount: Int
)

data class OrganisationEdge(
    val node: OrganisationType,
    val cursor: String
)

data class PageInfo(
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val startCursor: String?,
    val endCursor: String?
)

// Extension functions for mapping
fun OrganisationResponse.toGraphQL(): OrganisationType = OrganisationType(
    id = ID(id.toString()),
    uuid = uuid,
    parentOrganisationId = parentOrganisationId,
    effectiveTimestamp = effectiveTimestamp
)
