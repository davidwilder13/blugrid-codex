package net.blugrid.api.common.model.resource

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.audit.ResourceAudit

@Schema(description = "Base audited resource")
interface BaseAuditedResource<T> : BaseResource<T> {
    @get:JsonIgnore
    val resourceType: ResourceType
    val audit: ResourceAudit?
}
