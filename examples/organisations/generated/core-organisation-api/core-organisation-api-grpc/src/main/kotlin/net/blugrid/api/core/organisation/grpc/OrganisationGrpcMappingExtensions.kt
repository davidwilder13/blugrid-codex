package net.blugrid.api.core.organisation.grpc

import io.micronaut.data.model.Page
import net.blugrid.api.core.organisation.model.Organisation
import net.blugrid.api.core.organisation.model.OrganisationCreate
import net.blugrid.api.core.organisation.model.OrganisationUpdate
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

fun Organisation.toOrganisationResponse(): OrganisationResponse =
    OrganisationResponse.newBuilder()
        .setId(id)
        .setUuid(uuid.toString())
        .setParentOrganisationId(parentOrganisationId)
        .setEffectiveTimestamp(effectiveTimestamp.toString())
        .build()

fun OrganisationCreateRequest.toDomain(): OrganisationCreate =
    OrganisationCreate(
        uuid = UUID.randomUUID(),
        parentOrganisationId = parentOrganisationId,
        effectiveTimestamp = LocalDateTime.parse(effectiveTimestamp)
    )

fun OrganisationUpdateRequest.toDomain(): OrganisationUpdate =
    OrganisationUpdate(
        id = id,
        uuid = uuid.takeIf { it.isNotBlank() }?.let(UUID::fromString) ?: UUID.randomUUID(),
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
        .setTotalElements(totalSize.toInt())
        .setTotalPages(totalPages)
        .setPage(pageNumber)
        .setSize(size)
        .build()
