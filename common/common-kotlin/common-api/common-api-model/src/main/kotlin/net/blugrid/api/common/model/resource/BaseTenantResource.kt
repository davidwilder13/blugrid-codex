package net.blugrid.api.common.model.resource

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.audit.ResourceAudit
import net.blugrid.api.common.model.scope.TenantScope

@Schema(description = "Tenant resource")
abstract class BaseTenantResource<T>(
    @Schema(
        description = "Permissions associated with the tenant resource, defining access control and security settings.",
        nullable = true
    )
    open val scope: TenantScope?,

    @Schema(
        description = "Audit information for the tenant resource, including creation and modification details.",
        nullable = true
    )
    override val audit: ResourceAudit?
) : BaseAuditedResource<T>
