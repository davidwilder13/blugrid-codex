package net.blugrid.api.core.organisation.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Query filter for Organisation records")
data class OrganisationFilter(
    @Schema(description = "Only include organisations with these IDs")
    val ids: List<Long>? = null,

    @Schema(description = "Only include organisations with these UUIDs")
    val uuids: List<UUID>? = null,

    @Schema(description = "Only include organisations with these parent IDs")
    val parentOrganisationIds: List<Long>? = null,

    @Schema(description = "Include from this effective timestamp (inclusive)")
    val effectiveFrom: LocalDateTime? = null,

    @Schema(description = "Include until this effective timestamp (inclusive)")
    val effectiveTo: LocalDateTime? = null
)
