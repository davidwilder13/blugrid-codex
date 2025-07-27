package net.blugrid.api.core.organisation.grpc

import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import net.blugrid.common.model.pagination.Page
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

fun Organisation.toOrganisationResponse(): OrganisationResponse =
    OrganisationResponse.newBuilder()
        .setId(id.value)
        .setUuid(uuid.value.toString())
        .setParentOrganisationId(parentOrganisationId)
        .setEffectiveTimestamp(effectiveTimestamp.toString())
        .build()

fun OrganisationCreateRequest.toDomain(): OrganisationCreate =
    OrganisationCreate(
        uuid = IdentityUUID(UUID.randomUUID()),
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = LocalDateTime.parse(effectiveTimestamp)
    )

fun OrganisationUpdateRequest.toDomain(): OrganisationUpdate =
    OrganisationUpdate(
        id = IdentityID(id),
        uuid = IdentityUUID(
            uuid.takeIf { it.isNotBlank() }?.let(UUID::fromString) ?: UUID.randomUUID()
        ),
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = LocalDateTime.parse(effectiveTimestamp)
    )

fun Optional<Organisation>.toGrpcOptional(): OrganisationOptionalResponse =
    OrganisationOptionalResponse.newBuilder()
        .setExists(isPresent)
        .apply {
            ifPresent { this.organisation = it.toOrganisationResponse() }
        }
        .build()

fun List<Organisation>.toGrpcList(): OrganisationListResponse =
    OrganisationListResponse.newBuilder()
        .addAllOrganisations(map { it.toOrganisationResponse() })
        .build()

fun Page<Organisation>.toGrpcPage(): OrganisationPageResponse =
    OrganisationPageResponse.newBuilder()
        .addAllOrganisations(content.map { it.toOrganisationResponse() })
        .setTotalElements(totalElements.toInt())
        .setTotalPages(totalPages)
        .setPage(number)
        .setSize(size)
        .build()
