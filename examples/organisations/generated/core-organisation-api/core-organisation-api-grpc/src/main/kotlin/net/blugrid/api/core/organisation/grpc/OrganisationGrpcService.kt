package net.blugrid.api.core.organisation.grpc

import com.google.protobuf.Empty
import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import net.blugrid.api.core.organisation.service.OrganisationCommandService
import net.blugrid.api.core.organisation.service.OrganisationQueryService
import net.blugrid.common.model.pagination.Pageable
import net.blugrid.integration.grpc.mapper.toCommonSort
import net.blugrid.platform.logging.logger
import java.util.UUID

@Singleton
class OrganisationGrpcService(
    private val organisationCommandService: OrganisationCommandService,
    private val organisationQueryService: OrganisationQueryService,
    private val validator: Validator
) : OrganisationStateServiceGrpcKt.OrganisationStateServiceCoroutineImplBase() {

    val log = logger()

    override suspend fun getById(request: OrganisationRequestById): OrganisationResponse =
        organisationQueryService.getById(request.id).toOrganisationResponse()

    override suspend fun getByIdOptional(request: OrganisationRequestById): OrganisationOptionalResponse =
        organisationQueryService.getByIdOptional(request.id).toGrpcOptional()

    override suspend fun getByUuid(request: OrganisationRequestByUuid): OrganisationResponse =
        organisationQueryService.getByUuid(UUID.fromString(request.uuid)).toOrganisationResponse()

    override suspend fun getByUuidOptional(request: OrganisationRequestByUuid): OrganisationOptionalResponse =
        organisationQueryService.getByUuidOptional(UUID.fromString(request.uuid)).toGrpcOptional()

    override suspend fun getAll(request: Empty): OrganisationListResponse =
        organisationQueryService.getAll().toGrpcList()

    override suspend fun getPage(request: OrganisationPageRequest): OrganisationPageResponse {
        val pageable = Pageable.from(request.page, request.size, request.sort.toCommonSort())
        return organisationQueryService.getPage(pageable).toGrpcPage()
    }

    override suspend fun create(request: OrganisationCreateRequest): OrganisationResponse {
//        val model = GrpcValidator.validate(validator) { request.toDomain() }
        return organisationCommandService.create(request.toDomain()).toOrganisationResponse()
    }

    override suspend fun update(request: OrganisationUpdateRequest): OrganisationResponse {
//        val update = GrpcValidator.validate(validator) { request.toDomain() }
        return organisationCommandService.update(request.id, request.toDomain()).toOrganisationResponse()
    }

    override suspend fun delete(request: OrganisationDeleteRequest): Empty {
        organisationCommandService.delete(request.id)
        return Empty.getDefaultInstance()
    }
}
