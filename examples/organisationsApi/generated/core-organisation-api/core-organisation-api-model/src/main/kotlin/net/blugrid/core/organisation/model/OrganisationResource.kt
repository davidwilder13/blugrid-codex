
package net.blugrid.core.organisation.model.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.resource.Audit
import net.blugrid.api.common.model.resource.UnscopedResource
import net.blugrid.api.common.model.resource.ResourceType
import java.util.UUID
import [object Object]

@Schema(description = "Represents a organisation within the system.")
data class Organisation(

    override var id: Long,

    override var uuid: UUID,

    @Schema(description = "The ID of the parent organisation.", example = "1001")
    var parentOrganisationId: Long,

    @Schema(description = "The date and time the organisation becomes active.", example = "2024-08-25T14:15:22")
    var effectiveTimestamp: LocalDateTime,

    override val audit: Audit? = null
) : UnscopedResource<Organisation>(audit) {

    @get:JsonIgnore
    override val resourceType: ResourceType
        get() = ResourceType.ORGANISATION
}
