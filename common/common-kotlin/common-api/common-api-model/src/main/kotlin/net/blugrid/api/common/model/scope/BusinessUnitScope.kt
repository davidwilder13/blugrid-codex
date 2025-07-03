package net.blugrid.api.common.model.scope

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Represents the Scopes associated with a business unit within a tenant.")
class BusinessUnitScope(
    @Schema(description = "The unique identifier of the tenant.", example = "1001")
    override var tenantId: Long? = null,

    @Schema(description = "The unique identifier of the business unit within the tenant.", example = "2001")
    var businessUnitId: Long? = null,
) : TenantScope(tenantId)
