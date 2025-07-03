package net.blugrid.api.common.model.audit

import net.blugrid.api.common.model.resource.ResourceType
import java.time.LocalDateTime

class AuditEventLog(
    val auditEventType: AuditEventType,
    val auditEventTimestamp: LocalDateTime,
    val resourceType: ResourceType,
    val resourceId: Long,
    val resource: Any,
    val tenantId: Long,
    val sessionId: Long,
    val version: Int? = 0
)
