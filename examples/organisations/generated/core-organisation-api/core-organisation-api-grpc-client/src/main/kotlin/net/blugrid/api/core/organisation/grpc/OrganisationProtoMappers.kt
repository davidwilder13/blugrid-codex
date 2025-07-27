package net.blugrid.api.core.organisation.grpc

import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import net.blugrid.common.model.pagination.Page
import net.blugrid.common.model.pagination.Pageable
import net.blugrid.integration.grpc.mapper.toProto
import java.time.LocalDateTime
import java.util.UUID

fun OrganisationResponse.toOrganisation(): Organisation =
    Organisation(
        id = IdentityID(this.id),
        uuid = IdentityUUID(UUID.fromString(this.uuid)),
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = LocalDateTime.parse(this.effectiveTimestamp)
    )

fun OrganisationCreateRequest.toOrganisationCreate(): OrganisationCreate =
    OrganisationCreate(
        uuid = IdentityUUID(UUID.randomUUID()), // or fromString if proto has uuid field
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = LocalDateTime.parse(this.effectiveTimestamp)
    )

fun OrganisationUpdateRequest.toOrganisationUpdate(): OrganisationUpdate =
    OrganisationUpdate(
        id = IdentityID(this.id),
        uuid = IdentityUUID(UUID.fromString(this.uuid)),
        parentOrganisationId = this.parentOrganisationId,
        effectiveTimestamp = LocalDateTime.parse(this.effectiveTimestamp)
    )

// Model -> gRPC

fun Pageable.toOrganisationPageRequest(): OrganisationPageRequest {
    return OrganisationPageRequest.newBuilder()
        .setPage(this.number)
        .setSize(this.size)
        .setSort(this.sort.toProto())
        .build()
}

fun Organisation.toOrganisationResponse(): OrganisationResponse =
    OrganisationResponse.newBuilder()
        .setId(this.id.value)
        .setUuid(this.uuid.value.toString())
        .setParentOrganisationId(this.parentOrganisationId)
        .setEffectiveTimestamp(this.effectiveTimestamp.toString())
        .build()

fun OrganisationCreate.toOrganisationCreateRequest(): OrganisationCreateRequest =
    OrganisationCreateRequest.newBuilder()
        .setUuid(this.uuid.toString())
        .setParentOrganisationId(this.parentOrganisationId)
        .setEffectiveTimestamp(this.effectiveTimestamp.toString())
        .build()

fun OrganisationUpdate.toOrganisationUpdateRequest(): OrganisationUpdateRequest =
    OrganisationUpdateRequest.newBuilder()
        .setId(this.id.value)
        .setUuid(this.uuid.value.toString())
        .setParentOrganisationId(this.parentOrganisationId)
        .setEffectiveTimestamp(this.effectiveTimestamp.toString())
        .build()

fun Page<Organisation>.toGrpcPage(): OrganisationPageResponse =
    OrganisationPageResponse.newBuilder()
        .addAllOrganisations(content.map { it.toOrganisationResponse() })
        .setTotalElements(totalElements.toInt())
        .setTotalPages(totalPages)
        .setPage(number)
        .setSize(size)
        .build()
