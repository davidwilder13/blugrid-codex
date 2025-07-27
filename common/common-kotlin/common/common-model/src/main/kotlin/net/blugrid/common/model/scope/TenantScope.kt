package net.blugrid.common.model.scope

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.common.domain.IdentityID


@Schema(description = "Represents the base Scopes associated with a tenant in a multi-tenant environment.")
open class TenantScope(
    @Schema(description = "The unique identifier of the tenant.", example = "1001")
    open var tenantId: IdentityID? = null,
) {
    @Schema(description = "Indicates whether the resource is external to the tenant.", example = "false")
    var isExternalResource: Boolean? = null
}
