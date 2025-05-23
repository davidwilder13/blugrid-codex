
package net.blugrid.core.organisation.model.model

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.resource.GenericCreateResource
import java.util.UUID
import [object Object]

@Schema(description = "Model used to create a new organisation.")
data class OrganisationCreate(

    @Schema(description = "The globally unique identifier for this organisation.", example = "123e4567-e89b-12d3-a456-426614174000")
    override var uuid: UUID,

    @Schema(description = "The ID of the parent organisation.", example = "1001")
    var parentOrganisationId: Long,

    @Schema(description = "The date and time the organisation becomes active.", example = "2024-08-25T14:15:22")
    var effectiveTimestamp: LocalDateTime,

) : GenericCreateResource<OrganisationCreate>(uuid)
