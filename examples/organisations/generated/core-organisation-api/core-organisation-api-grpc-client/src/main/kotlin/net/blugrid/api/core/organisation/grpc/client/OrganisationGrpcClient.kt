package net.blugrid.api.core.organisation.grpc.client

import com.google.protobuf.Empty
import net.blugrid.api.core.organisation.grpc.OrganisationCreateRequest
import net.blugrid.api.core.organisation.grpc.OrganisationListResponse
import net.blugrid.api.core.organisation.grpc.OrganisationOptionalResponse
import net.blugrid.api.core.organisation.grpc.OrganisationPageRequest
import net.blugrid.api.core.organisation.grpc.OrganisationPageResponse
import net.blugrid.api.core.organisation.grpc.OrganisationResponse
import net.blugrid.api.core.organisation.grpc.OrganisationStateServiceGrpcKt
import net.blugrid.api.core.organisation.grpc.OrganisationUpdateRequest
import net.blugrid.api.core.organisation.grpc.organisationDeleteRequest
import net.blugrid.api.core.organisation.grpc.organisationRequestById
import net.blugrid.api.core.organisation.grpc.organisationRequestByIds
import net.blugrid.api.core.organisation.grpc.organisationRequestByUuid
import java.util.UUID

class OrganisationGrpcClient(
    private val stub: OrganisationStateServiceGrpcKt.OrganisationStateServiceCoroutineStub
) {
    suspend fun getById(id: Long): OrganisationResponse =
        stub.getById(organisationRequestById { this.id = id })

    suspend fun getByUuid(uuid: UUID): OrganisationResponse =
        stub.getByUuid(organisationRequestByUuid { this.uuid = uuid.toString() })

    suspend fun getByIdOptional(id: Long): OrganisationOptionalResponse =
        stub.getByIdOptional(organisationRequestById { this.id = id })

    suspend fun getByUuidOptional(uuid: UUID): OrganisationOptionalResponse =
        stub.getByUuidOptional(organisationRequestByUuid { this.uuid = uuid.toString() })

    suspend fun getByIds(ids: List<Long>): List<OrganisationResponse> {
        if (ids.isEmpty()) return emptyList()
        return stub.getByIds(organisationRequestByIds { this.ids.addAll(ids) }).organisationsList
    }

    suspend fun getPage(request: OrganisationPageRequest): OrganisationPageResponse =
        stub.getPage(request)

    suspend fun getAll(): OrganisationListResponse =
        stub.getAll(Empty.getDefaultInstance())

    suspend fun create(request: OrganisationCreateRequest): OrganisationResponse =
        stub.create(request)

    suspend fun update(request: OrganisationUpdateRequest): OrganisationResponse =
        stub.update(request)

    suspend fun delete(id: Long) {
        stub.delete(organisationDeleteRequest { this.id = id })
    }
}
