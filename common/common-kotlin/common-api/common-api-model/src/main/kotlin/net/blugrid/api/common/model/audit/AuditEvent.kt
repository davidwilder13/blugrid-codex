package net.blugrid.api.common.model.audit

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.context.event.ApplicationEvent
import net.blugrid.api.common.model.resource.GenericAuditedResource
import net.blugrid.api.common.model.resource.ResourceType
import java.time.LocalDateTime

class AuditEvent(
    val auditEventType: AuditEventType,
    val auditEventTimestamp: LocalDateTime,
    val resourceType: ResourceType,
    val resourceId: Long,
    val resource: GenericAuditedResource<*>,
    val tenantId: Long,
    val sessionId: Long,
    val version: Int? = null
) : ApplicationEvent(resource)

enum class AuditEventType {
    @JsonProperty("CREATE")
    CREATE,

    @JsonProperty("UPDATE")
    UPDATE,

    @JsonProperty("DELETE")
    DELETE
}

