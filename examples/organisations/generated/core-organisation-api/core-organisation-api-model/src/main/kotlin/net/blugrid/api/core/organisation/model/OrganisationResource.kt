package net.blugrid.api.core.organisation.model

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import net.blugrid.common.model.audit.ResourceAudit
import net.blugrid.common.model.resource.ResourceType
import net.blugrid.common.model.resource.UnscopedResource
import java.time.LocalDateTime

@Schema(description = "Represents a organisation within the system.")
data class Organisation(

    override var id: IdentityID,

    override var uuid: IdentityUUID,

    @Schema(description = "The ID of the parent organisation.", example = "1001")
    var parentOrganisationId: Long,

    @Schema(description = "The date and time the organisation becomes active.", example = "2024-08-25")
    var effectiveTimestamp: LocalDateTime,

    override val audit: ResourceAudit? = null
) : UnscopedResource<Organisation>(audit) {

    override val resourceType: ResourceType
        get() = ResourceType.ORGANISATION
}
