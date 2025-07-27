package net.blugrid.common.model.audit

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Audit metadata tracking resource creation and modification.")
data class ResourceAudit(
    @Schema(description = "Optimistic locking version.", example = "1")
    val version: Int = 0,

    @Schema(description = "Who and when the resource was created.")
    val created: AuditStamp? = null,

    @Schema(description = "Who and when the resource was last changed.")
    val lastChanged: AuditStamp? = null
)
