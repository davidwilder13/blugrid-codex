package net.blugrid.api.core.organisation.grpc

import com.google.protobuf.Empty
import jakarta.inject.Singleton
import net.blugrid.api.core.organisation.service.OrganisationCommandService
import net.blugrid.api.core.organisation.service.OrganisationQueryService
import net.blugrid.common.model.pagination.Pageable
import net.blugrid.integration.grpc.mapper.toCommonSort
import net.blugrid.integration.grpc.service.GrpcService
import net.blugrid.integration.grpc.service.GrpcServiceExecutor
import net.blugrid.platform.logging.logger
import java.util.UUID

@Singleton
class OrganisationGrpcService(
    private val organisationCommandService: OrganisationCommandService,
    private val organisationQueryService: OrganisationQueryService,
    override val grpcExecutor: GrpcServiceExecutor
) : OrganisationStateServiceGrpcKt.OrganisationStateServiceCoroutineImplBase(),
    GrpcService {

    val log = logger()

    override suspend fun getById(request: OrganisationRequestById): OrganisationResponse =
        grpcCall("getById") {
            organisationQueryService.getById(request.id).toOrganisationResponse()
        }

    override suspend fun getByIdOptional(request: OrganisationRequestById): OrganisationOptionalResponse =
        grpcCall("getByIdOptional") {
            organisationQueryService.getByIdOptional(request.id).toGrpcOptional()
        }

    override suspend fun getByIds(request: OrganisationRequestByIds): OrganisationListResponse =
        grpcCall("getByIds") {
            organisationQueryService.getByIds(request.idsList).toGrpcList()
        }

    override suspend fun getByUuid(request: OrganisationRequestByUuid): OrganisationResponse =
        grpcCall("getByUuid") {
            organisationQueryService.getByUuid(UUID.fromString(request.uuid)).toOrganisationResponse()
        }

    override suspend fun getByUuidOptional(request: OrganisationRequestByUuid): OrganisationOptionalResponse =
        grpcCall("getByUuidOptional") {
            organisationQueryService.getByUuidOptional(UUID.fromString(request.uuid)).toGrpcOptional()
        }

    override suspend fun getAll(request: Empty): OrganisationListResponse =
        grpcCall("getAll") {
            organisationQueryService.getAll().toGrpcList()
        }

    override suspend fun getPage(request: OrganisationPageRequest): OrganisationPageResponse =
        grpcCall("getPage") {
            val pageable = Pageable.from(request.page, request.size, request.sort.toCommonSort())
            organisationQueryService.getPage(pageable).toGrpcPage()
        }

    override suspend fun create(request: OrganisationCreateRequest): OrganisationResponse =
        grpcCall("create") {
            val domainModel = request.toDomain()
            organisationCommandService.create(domainModel).toOrganisationResponse()
        }

    override suspend fun update(request: OrganisationUpdateRequest): OrganisationResponse =
        grpcCall("update") {
            organisationCommandService.update(request.id, request.toDomain()).toOrganisationResponse()
        }

    override suspend fun delete(request: OrganisationDeleteRequest): Empty =
        grpcCall("delete") {
            organisationCommandService.delete(request.id)
            Empty.getDefaultInstance()
        }
}
