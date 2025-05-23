
package net.blugrid.core.organisation.model.model

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.resource.GenericUpdateResource
import java.util.UUID
import [object Object]

@Schema(description = "Model used to update an existing organisation.")
data class OrganisationUpdate(

    override var id: Long,

    override var uuid: UUID,

    @Schema(description = "The ID of the parent organisation.", example = "1001")
    var parentOrganisationId: Long,

    @Schema(description = "The date and time the organisation becomes active.", example = "2024-08-25T14:15:22")
    var effectiveTimestamp: LocalDateTime,

) : GenericUpdateResource<OrganisationUpdate>(id, uuid)
