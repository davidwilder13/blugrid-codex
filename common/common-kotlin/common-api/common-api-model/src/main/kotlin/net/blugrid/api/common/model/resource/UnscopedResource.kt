package net.blugrid.api.common.model.resource

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.audit.ResourceAudit

@Schema(description = "Unscoped resource")
abstract class UnscopedResource<T>(
    @Schema(
        description = "Audit information for the unscoped resource, including creation and modification details.",
        nullable = true
    )
    override val audit: ResourceAudit?
) : BaseAuditedResource<T>
