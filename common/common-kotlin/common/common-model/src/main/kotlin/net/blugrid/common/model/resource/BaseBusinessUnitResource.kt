package net.blugrid.common.model.resource

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.common.model.audit.ResourceAudit
import net.blugrid.common.model.scope.BusinessUnitScope

@Schema(description = "Business unit resource")
abstract class BaseBusinessUnitResource<T>(
    @Schema(
        description = "Permissions associated with the business unit resource, defining access control and security settings.",
        nullable = true
    )
    open val scope: BusinessUnitScope?,

    @Schema(
        description = "Audit information for the business unit resource, including creation and modification details.",
        nullable = true
    )
    override val audit: ResourceAudit?
) : BaseAuditedResource<T>
