package net.blugrid.api.core.organisation.model

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.resource.BaseCreateResource
import net.blugrid.common.domain.IdentityUUID
import java.time.LocalDateTime

@Schema(description = "Model used to create a new organisation.")
data class OrganisationCreate(

    @Schema(description = "The globally unique identifier for this organisation.", example = "123e4567-e89b-12d3-a456-426614174000")
    override var uuid: IdentityUUID,

    @Schema(description = "The ID of the parent organisation.", example = "1001")
    var parentOrganisationId: Long,

    @Schema(description = "The date and time the organisation becomes active.", example = "2024-08-25")
    var effectiveTimestamp: LocalDateTime,

    ) : BaseCreateResource<OrganisationCreate>(uuid)
