package net.blugrid.common.model.audit

import io.micronaut.context.event.ApplicationEvent
import net.blugrid.common.model.resource.BaseAuditedResource
import net.blugrid.common.model.resource.ResourceType
import net.blugrid.common.domain.IdentityID
import java.time.LocalDateTime
class AuditEvent(
    val auditEventType: AuditEventType,
    val auditEventTimestamp: LocalDateTime,
    val resourceType: ResourceType,
    val resourceId: IdentityID,
    val resource: BaseAuditedResource<*>,
    val tenantId: IdentityID,
    val sessionId: IdentityID,
    val version: Int? = null
) : ApplicationEvent(resource)

