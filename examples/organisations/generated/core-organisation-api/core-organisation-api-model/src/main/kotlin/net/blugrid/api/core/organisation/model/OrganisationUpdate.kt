package net.blugrid.api.core.organisation.model

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.resource.BaseUpdateResource
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import java.time.LocalDateTime

@Schema(description = "Model used to update an existing organisation.")
data class OrganisationUpdate(

    override var id: IdentityID,

    override var uuid: IdentityUUID,

    @Schema(description = "The ID of the parent organisation.", example = "1001")
    var parentOrganisationId: Long,

    @Schema(description = "The date and time the organisation becomes active.", example = "2024-08-25")
    var effectiveTimestamp: LocalDateTime,

    ) : BaseUpdateResource<OrganisationUpdate>(id, uuid)
